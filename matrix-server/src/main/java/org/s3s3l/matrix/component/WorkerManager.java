package org.s3s3l.matrix.component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.annotation.PreDestroy;

import org.s3s3l.matrix.api.worker.WorkType;
import org.s3s3l.matrix.configuration.config.WorkersConfig;
import org.s3s3l.matrix.utils.influxdb.InfluxDBConfig;
import org.s3s3l.matrix.utils.kafka.KafkaConfig;
import org.s3s3l.matrix.utils.metric.MetricHubManager;
import org.s3s3l.matrix.utils.verify.Verifier;
import org.s3s3l.matrix.utils.worker.Worker;
import org.s3s3l.matrix.utils.worker.config.Kafka2InfluxDBWorkerConfig;
import org.s3s3l.matrix.utils.worker.config.Kafka2MetricsWorkerConfig;
import org.s3s3l.matrix.utils.worker.config.Metrics2InfluxDBWorkerConfig;
import org.s3s3l.matrix.utils.worker.exception.WorkerGeneratingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
public class WorkerManager implements ApplicationListener<ApplicationStartedEvent> {

    @Autowired
    private KafkaConfig kafkaConfig;
    @Autowired
    private InfluxDBConfig influxDBConfig;
    @Autowired
    private WorkersConfig workersConfig;
    @Autowired
    private Verifier verifier;
    @Autowired
    private MetricHubManager metricHubManager;

    private ScheduledExecutorService scheduledExecutorService;

    /**
     * {type: {id: worker}}
     */
    @Getter
    private final Map<WorkType, Map<String, Worker<?>>> workers = new ConcurrentHashMap<>();

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {

        // 初始化定时任务线程池
        scheduledExecutorService = new ScheduledThreadPoolExecutor(
                workersConfig.getKafka2Influxdb().size() + workersConfig.getKafka2Metrics().size());

        // 初始化kafka到influxdb的worker
        for (Kafka2InfluxDBWorkerConfig config : workersConfig.getKafka2Influxdb()) {
            verifier.verify(config, Kafka2InfluxDBWorkerConfig.class);
            config.setWorkType(WorkType.KAFKA_TO_INFLUXDB);
            try {
                Worker<?> worker = config.getType()
                        .getConstructor(Kafka2InfluxDBWorkerConfig.class,
                                KafkaConfig.class,
                                InfluxDBConfig.class,
                                ScheduledExecutorService.class)
                        .newInstance(config, kafkaConfig, influxDBConfig,
                                scheduledExecutorService);
                workers.computeIfAbsent(config.getWorkType(), type -> new ConcurrentHashMap<>()).put(
                        worker.getId(), worker);
                worker.start();
            } catch (Exception e) {
                throw new WorkerGeneratingException(e);
            }
        }

        // 初始化kafka到内存指标的worker
        for (Kafka2MetricsWorkerConfig config : workersConfig.getKafka2Metrics()) {
            verifier.verify(config, Kafka2MetricsWorkerConfig.class);

            config.setWorkType(WorkType.KAFKA_TO_METRICS);
            try {
                Worker<?> worker = config.getType()
                        .getConstructor(Kafka2MetricsWorkerConfig.class,
                                KafkaConfig.class,
                                MetricHubManager.class)
                        .newInstance(config, kafkaConfig, metricHubManager);
                workers.computeIfAbsent(config.getWorkType(), type -> new ConcurrentHashMap<>()).put(
                        worker.getId(), worker);
                worker.start();
            } catch (Exception e) {
                throw new WorkerGeneratingException(e);
            }
        }

        // 初始化内存指标到influxdb的worker
        for (Metrics2InfluxDBWorkerConfig config : workersConfig.getMetrics2Influxdb()) {
            verifier.verify(config, Metrics2InfluxDBWorkerConfig.class);

            config.setWorkType(WorkType.METRICS_TO_INFLUXDB);
            try {
                Worker<?> worker = config.getType()
                        .getConstructor(Metrics2InfluxDBWorkerConfig.class,
                                MetricHubManager.class,
                                InfluxDBConfig.class,
                                ScheduledExecutorService.class)
                        .newInstance(config, metricHubManager, influxDBConfig, scheduledExecutorService);
                workers.computeIfAbsent(config.getWorkType(), type -> new ConcurrentHashMap<>()).put(
                        worker.getId(), worker);
                worker.start();
            } catch (Exception e) {
                throw new WorkerGeneratingException(e);
            }
        }
    }

    @PreDestroy
    public void destroy() {
        // 销毁定时任务线程池
        scheduledExecutorService.shutdown();
    }

}
