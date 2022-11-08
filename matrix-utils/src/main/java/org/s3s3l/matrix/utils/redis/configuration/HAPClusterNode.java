package org.s3s3l.matrix.utils.redis.configuration;

import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import lombok.Data;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;

/**
 * <p>
 * </p>
 * ClassName: HAPClusterNode
 *
 * @author carterwang
 * @version 1.0.0
 * @since JDK 1.8
 */
@Data
public class HAPClusterNode {

    private String password;
    private int connectionTimeout = Protocol.DEFAULT_TIMEOUT; //ms
    private int soTimeout = Protocol.DEFAULT_TIMEOUT; //ms
    private int maxAttempts = 5;
    private Set<HAPNode> clusterConfig;
    private GenericObjectPoolConfig<Jedis> poolConfig;
}
