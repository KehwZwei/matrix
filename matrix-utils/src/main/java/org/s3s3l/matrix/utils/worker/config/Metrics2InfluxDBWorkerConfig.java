package org.s3s3l.matrix.utils.worker.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Metrics2InfluxDBWorkerConfig extends DistributedWorkerConfig {
    private String metricName;
    /**
     * 采集间隔，单位：秒<br>
     */
    @Builder.Default
    private int metricInterval = 1;
    private String measurement;
}
