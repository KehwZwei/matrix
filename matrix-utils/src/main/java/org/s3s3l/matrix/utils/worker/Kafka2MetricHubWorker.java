package org.s3s3l.matrix.utils.worker;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Properties;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.s3s3l.matrix.api.worker.Status;
import org.s3s3l.matrix.utils.kafka.KafkaConfig;
import org.s3s3l.matrix.utils.kafka.KafkaConfig.ConsumerConfig;
import org.s3s3l.matrix.utils.metric.MetricHub;
import org.s3s3l.matrix.utils.metric.MetricHubManager;
import org.s3s3l.matrix.utils.stuctural.jackson.JacksonUtils;
import org.s3s3l.matrix.utils.worker.config.Kafka2MetricsWorkerConfig;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Kafka2MetricHubWorker extends DistributedWorker<Kafka2MetricsWorkerConfig> {
    protected final KafkaConsumer<String, byte[]> consumer;
    protected final MetricHub metricHub;

    protected Thread workThread;

    public Kafka2MetricHubWorker(
            Kafka2MetricsWorkerConfig workerConfig, KafkaConfig kafkaConfig,
            MetricHubManager metricHubManager) {
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

        // 获取指标管理器
        this.metricHub = metricHubManager.get(workerConfig.getMetricName());

        // 注册ShutdownHook，在进程结束时执行清理操作
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    protected JsonNode convertMessage(ConsumerRecord<String, byte[]> record) {
        return JacksonUtils.DEFAULT.toTreeNode(record.value());
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
                    for (ConsumerRecord<String, byte[]> record : pollRes) {
                        try {
                            JsonNode metric = convertMessage(record);
                            if (metric != null) {
                                // 提交指标
                                metricHub.putMetric(metric);
                            }
                        } catch (Exception e) {
                            // ignore
                            log.warn("消息转化失败，丢弃。", e);
                            continue;
                        }
                    }
                    consumer.commitSync();
                    if (count > 0) {
                        log.debug("消费成功: {}", count);
                    }
                } catch (Exception e) {
                    log.error("消费失败", e);
                }
            }

        }, workerConfig.getGroup() + workerConfig.getName() + "-WorkThread");

        workThread.start();

        return true;
    }

    @Override
    public boolean destroyInternal() {
        consumer.close();
        log.info("Worker {} destroyed.", workerConfig.getName());
        return true;
    }

    @Override
    public Kafka2MetricsWorkerConfig getConfig() {
        return super.getWorkerConfig();
    }

    @Override
    protected boolean doStop() {
        return true;
    }
}
