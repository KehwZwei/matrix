package org.s3s3l.matrix.utils.worker.config;

import java.util.Set;

import org.s3s3l.matrix.utils.annotations.Examine;
import org.s3s3l.matrix.utils.annotations.Expectation;
import org.s3s3l.matrix.utils.influxdb.InfluxDBConfig;
import org.s3s3l.matrix.utils.kafka.KafkaConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Kafka2InfluxDBWorkerConfig extends DistributedWorkerConfig {
    @Builder.Default
    @Examine(value = Expectation.LARGER_THAN, standard = 1)
    private int minCountPerFlush = 1_000;
    @Examine(value = Expectation.HAS_LENGTH, msg = "请指定worker的分组")
    private String group;
    @Examine(value = Expectation.HAS_LENGTH, msg = "请指定需要监听的topics")
    private Set<String> topics;
    private String measurement;
    private KafkaConfig kafkaConfig;
    private InfluxDBConfig influxDBConfig;
}
