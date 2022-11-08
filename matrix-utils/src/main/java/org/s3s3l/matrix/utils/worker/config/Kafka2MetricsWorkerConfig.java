package org.s3s3l.matrix.utils.worker.config;

import java.util.Set;

import org.s3s3l.matrix.utils.annotations.Examine;
import org.s3s3l.matrix.utils.annotations.Expectation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Kafka2MetricsWorkerConfig extends DistributedWorkerConfig {
    @Examine(value = Expectation.HAS_LENGTH, msg = "请指定指标名称")
    private String metricName;
    @Examine(value = Expectation.HAS_LENGTH, msg = "请指定worker的分组")
    private String group;
    @Examine(value = Expectation.HAS_LENGTH, msg = "请指定需要监听的topics")
    private Set<String> topics;
}
