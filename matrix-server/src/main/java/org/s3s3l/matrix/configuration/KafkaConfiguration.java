package org.s3s3l.matrix.configuration;

import org.s3s3l.matrix.utils.kafka.KafkaConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfiguration {
    private static final String CONFIG_PREFIX = "matrix.kafka";

    @Bean
    @ConfigurationProperties(prefix = CONFIG_PREFIX)
    @ConditionalOnMissingBean(KafkaConfig.class)
    public KafkaConfig redisConfig() {
        return new KafkaConfig();
    }
}
