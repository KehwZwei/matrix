package org.s3s3l.matrix.configuration.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = ZookeeperConfig.PREFIX)
public class ZookeeperConfig {
    public static final String PREFIX = "matrix.zookeeper";

    private String endPoint;
    private int retryTimes = 3;
    private int retryWaitMills = 100;
}
