package org.s3s3l.matrix.utils.worker;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.s3s3l.matrix.api.worker.Status;
import org.s3s3l.matrix.utils.influxdb.BasicRecord;
import org.s3s3l.matrix.utils.influxdb.InfluxDBConfig;
import org.s3s3l.matrix.utils.influxdb.InfluxDBHelper;
import org.s3s3l.matrix.utils.kafka.KafkaConfig;
import org.s3s3l.matrix.utils.kafka.KafkaConfig.ConsumerConfig;
import org.s3s3l.matrix.utils.worker.config.Kafka2InfluxDBWorkerConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class Kafka2InfluxDBWorker extends DistributedWorker<Kafka2InfluxDBWorkerConfig> {
    private final Map<String, List<Object>> recordsCache = new HashMap<>();
    private final Map<String, Object> locks = new ConcurrentHashMap<>();

    protected final KafkaConsumer<String, byte[]> consumer;
    protected final InfluxDBHelper influxDBHelper;

    protected Thread workThread;

    protected Kafka2InfluxDBWorker(Kafka2InfluxDBWorkerConfig workerConfig, KafkaConfig kafkaConfig,
            InfluxDBConfig influxDBConfig, ScheduledExecutorService scheduledExecutorService) {
        super(workerConfig);

        // 启动consumer
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaConfig.getBootstrapServers());
        props.putAll(KafkaConfig.DEFAULT_CONFIG);
        props.putAll(ConsumerConfig.DEFAULT_CONFIG);
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG, workerConfig.getGroup());
        this.consumer = new KafkaConsumer<>(props);
        consumer.subscribe(workerConfig.getTopics());

        // 启动influxdb客户端
        this.influxDBHelper = new InfluxDBHelper(influxDBConfig);

        // 启动定时降缓存中的记录提交到influxdb的任务，防止数据量较小时，缓存数量长时间无法达到minCountPerFlush，导致数据长时间驻留缓存而没有提交到influxdb
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (status() != Status.RUNNING) {
                return;
            }
            for (String measurement : recordsCache.keySet()) {
                synchronized (locks.computeIfAbsent(measurement, key -> new Object())) {
                    flushToInfluxdb(measurement);
                }
            }
        }, 10, 60, TimeUnit.SECONDS);

        // 注册ShutdownHook，在进程结束时执行清理操作
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    abstract protected List<? extends BasicRecord> convertMessage(ConsumerRecord<String, byte[]> record);

    private String getMeasurement(String suffix) {
        return StringUtils.isEmpty(suffix) || "-".equals(StringUtils.trim(suffix)) ? workerConfig.getMeasurement()
                : String.join("-", workerConfig.getMeasurement(), suffix);
    }

    /**
     * 提交记录到内存缓存
     * 
     * @param records
     */
    private void putToCache(List<BasicRecord> records) {
        Map<String, List<BasicRecord>> recordsMap = new HashMap<>();
        for (BasicRecord record : records) {
            recordsMap.computeIfAbsent(record.getMeasurementSuffix(), key -> new LinkedList<>()).add(record);
        }

        for (Entry<String, List<BasicRecord>> entry : recordsMap.entrySet()) {
            String measurement = getMeasurement(entry.getKey());
            // 对每个measurement加锁，防止数组的并发操作
            synchronized (locks.computeIfAbsent(measurement, key -> new Object())) {
                List<Object> cachedRecords = recordsCache.computeIfAbsent(measurement, key -> new LinkedList<>());
                cachedRecords.addAll(entry.getValue());
                if (cachedRecords.size() >= workerConfig.getMinCountPerFlush()) {
                    flushToInfluxdb(measurement);
                }
            }
        }
    }

    /**
     * 将指定measurement的数据从内存缓存提交到influxdb
     * 
     * @param measurement
     */
    private void flushToInfluxdb(String measurement) {
        List<Object> records = recordsCache.get(measurement);
        influxDBHelper.multiAdd(measurement, records);
        log.info("Flush to influxdb. measurement: {}, count: {}", measurement, records.size());
        records.clear();
    }

    @Override
    public boolean doStart() {
        // 启动kafka consumer拉取的工作线程
        workThread = new Thread(() -> {
            while (status() == Status.RUNNING) {
                try {
                    ConsumerRecords<String, byte[]> pollRes = consumer
                            .poll(Duration.of(100, ChronoUnit.MILLIS));
                    int count = pollRes.count();
                    List<BasicRecord> metrics = new LinkedList<>();
                    for (ConsumerRecord<String, byte[]> record : pollRes) {
                        try {
                            metrics.addAll(convertMessage(record));
                        } catch (Exception e) {
                            // ignore
                            log.warn("消息转化失败，丢弃。", e);
                            continue;
                        }
                    }
                    if (!metrics.isEmpty()) {
                        putToCache(metrics);
                    }
                    consumer.commitSync();
                    if (count > 0) {
                        log.debug("消费成功: {}", count);
                    }
                } catch (Exception e) {
                    log.error("消费失败", e);
                }
            }

        }, workerConfig.getGroup() + workerConfig.getMeasurement() + "-WorkThread");

        workThread.start();
        return true;
    }

    @Override
    public boolean destroyInternal() {
        // 销毁前尝试将内存缓存中的所有数据提交到influxdb
        try {
            for (String measurement : recordsCache.keySet()) {
                synchronized (locks.computeIfAbsent(measurement, key -> new Object())) {
                    flushToInfluxdb(measurement);
                }
            }
            influxDBHelper.close();
        } catch (Exception e) {
            log.warn("Fail to close InfluxDBHelper.", e);
        }
        consumer.close();
        log.info("Worker {} destroyed.", workerConfig.getName());
        return true;
    }

    @Override
    public Kafka2InfluxDBWorkerConfig getConfig() {
        return super.getWorkerConfig();
    }

    @Override
    protected boolean doStop() {
        return true;
    }
}
