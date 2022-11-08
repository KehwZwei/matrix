package org.s3s3l.matrix.utils.metric.config;

import java.util.List;

import org.s3s3l.matrix.utils.field.TimeFieldConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MetricHubConfig {
    private String name;
    private String prefix;
    /**
     * 时间字段配置，空的时候使用当前服务器时间
     */
    private TimeFieldConfig timeField;
    private List<MetricConfig> metrics;
    @Builder.Default
    private int timeRangePerSlider = 256;
}
