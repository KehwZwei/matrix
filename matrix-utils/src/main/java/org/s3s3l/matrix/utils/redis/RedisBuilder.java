package org.s3s3l.matrix.utils.redis;

import org.s3s3l.matrix.utils.redis.base.IRedis;
import org.s3s3l.matrix.utils.redis.configuration.RedisConfig;

public abstract class RedisBuilder {

    public static IRedis newInstance(RedisConfig config) {
        switch (config.getType()) {
            case CLUSTER: {
                RedisClusterHelper redis = new RedisClusterHelper();
                redis.init(config.getCluster());
                return redis;
            }
            case MASTER_SLAVE: {
                RedisHelper redis = new RedisHelper();
                redis.init(config.getMasterSlave());
                return redis;
            }
            default:
                throw new RuntimeException("redis type not supported");
        }
    }

    public static IRedis newInstance(RedisConfig config, int maxtotal) {
        switch (config.getType()) {
            case CLUSTER: {
                RedisClusterHelper redis = new RedisClusterHelper();
                config.getCluster().getPoolConfig().setMaxTotal(maxtotal);
                redis.init(config.getCluster());
                return redis;
            }
            case MASTER_SLAVE: {
                RedisHelper redis = new RedisHelper();
                config.getMasterSlave().getMaster().getPoolConfig().setMaxTotal(maxtotal);;
                if (config.getMasterSlave().getSlave() != null) {
                    config.getMasterSlave().getSlave().getPoolConfig().setMaxTotal(maxtotal);
                }
                redis.init(config.getMasterSlave());
                return redis;
            }
            default:
                throw new RuntimeException("redis type not supported");
        }
    }
}
