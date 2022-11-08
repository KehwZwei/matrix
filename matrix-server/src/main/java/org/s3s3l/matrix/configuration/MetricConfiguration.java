package org.s3s3l.matrix.configuration;

import org.s3s3l.matrix.configuration.config.MetricsConfig;
import org.s3s3l.matrix.utils.metric.MetricHubManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricConfiguration {

    @Bean
    public MetricHubManager MetricHubManager(MetricsConfig metricsConfig) {

        return new MetricHubManager(metricsConfig.getHubs());
    }
}
