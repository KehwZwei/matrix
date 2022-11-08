package org.s3s3l.matrix.configuration.config;

import java.util.ArrayList;
import java.util.List;

import org.s3s3l.matrix.utils.worker.config.Kafka2InfluxDBWorkerConfig;
import org.s3s3l.matrix.utils.worker.config.Kafka2MetricsWorkerConfig;
import org.s3s3l.matrix.utils.worker.config.Metrics2InfluxDBWorkerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = WorkersConfig.PREFIX)
public class WorkersConfig {
    public static final String PREFIX = "workers";

    private List<Kafka2InfluxDBWorkerConfig> kafka2Influxdb = new ArrayList<>();

    private List<Kafka2MetricsWorkerConfig> kafka2Metrics = new ArrayList<>();

    private List<Metrics2InfluxDBWorkerConfig> metrics2Influxdb = new ArrayList<>();

}
