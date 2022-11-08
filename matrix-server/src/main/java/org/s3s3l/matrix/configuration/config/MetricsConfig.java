package org.s3s3l.matrix.configuration.config;

import java.util.List;

import org.s3s3l.matrix.utils.metric.config.MetricHubConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = MetricsConfig.PREFIX)
public class MetricsConfig {
    public static final String PREFIX = "metrics";

    private List<MetricHubConfig> hubs;
}
