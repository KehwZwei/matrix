package org.s3s3l.matrix.configuration;

import org.s3s3l.matrix.utils.influxdb.InfluxDBConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxDBConfiguration {
    private static final String CONFIG_PREFIX = "matrix.influxdb";
    
    @Bean
    @ConfigurationProperties(prefix = CONFIG_PREFIX)
    @ConditionalOnMissingBean(InfluxDBConfig.class)
    public InfluxDBConfig influxDBConfig() {
        return new InfluxDBConfig();
    }
}
