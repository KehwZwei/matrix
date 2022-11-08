package org.s3s3l.matrix.utils.redis.configuration;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.s3s3l.matrix.utils.bean.exception.ResourceNotFoundException;
import org.s3s3l.matrix.utils.file.FileUtils;
import org.s3s3l.matrix.utils.stuctural.jackson.JacksonHelper;
import org.s3s3l.matrix.utils.stuctural.jackson.JacksonUtils;
import org.springframework.beans.factory.InitializingBean;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;

@Slf4j
@Data
public class RedisConfig implements InitializingBean {
    private static final String[] EXTRA_CONFIG_FILES = new String[] { "redis.conf", "config/redis.conf",
            "conf/redis.conf" };

    private EnumRedisType type = EnumRedisType.CLUSTER;
    private RedisMasterSlaveConfig masterSlave;
    private HAPClusterNode cluster;

    @Data
    public static class RedisMasterSlaveConfig {
        private RedisProps master;
        private RedisProps slave;
    }

    @Data
    public static class RedisProps {
        private int soTimeout = Protocol.DEFAULT_TIMEOUT; // ms
        /**
         * Connection timeout.
         */
        private int timeout = Protocol.DEFAULT_TIMEOUT; // ms
        private int database = 0;

        private String host = "localhost";
        private String password;
        private int port = 6379;
        private boolean ssl;
        private String clientName;
        private GenericObjectPoolConfig<Jedis> poolConfig = new GenericObjectPoolConfig<>();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        JacksonHelper helper = JacksonUtils.create(new YAMLFactory());
        try {
            helper.update(this, FileUtils.getFirstExistResource(EXTRA_CONFIG_FILES));
        } catch (ResourceNotFoundException e) {
            // ignore
        } catch (Exception e) {
            log.warn("redis独立配置文件加载失败: {}", EXTRA_CONFIG_FILES, e);
        }
        log.info("redis配置： {}", helper.prettyPrinter().toStructuralString(this));
    }
}
