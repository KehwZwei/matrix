package org.s3s3l.matrix.configuration;

import java.util.List;
import java.util.Objects;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.s3s3l.matrix.configuration.config.ZookeeperConfig;
import org.s3s3l.matrix.utils.distribute.DistributedHelper;
import org.s3s3l.matrix.utils.distribute.event.BasicEvent;
import org.s3s3l.matrix.utils.distribute.event.BasicEventType;
import org.s3s3l.matrix.utils.distribute.key.KeyGenerator;
import org.s3s3l.matrix.utils.distribute.lock.DistributedLock;
import org.s3s3l.matrix.utils.distribute.register.Register;
import org.s3s3l.matrix.utils.reflect.scan.ClassScanner;
import org.s3s3l.matrix.utils.zookeeper.lock.ZkLock;
import org.s3s3l.matrix.utils.zookeeper.register.ZkRegister;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DistributedConfiguration {

    @Bean
    public CuratorFramework curatorFramework(ZookeeperConfig config) {
        CuratorFramework client = CuratorFrameworkFactory.newClient(config.getEndPoint(),
                new RetryNTimes(config.getRetryTimes(), config.getRetryWaitMills()));
        client.start();
        return client;
    }

    // Register

    @Bean
    public Register<byte[], BasicEventType, BasicEvent> zkRegister(CuratorFramework client) {
        return new ZkRegister(client);
    }

    // Lock

    @Bean
    public DistributedLock zkLock(CuratorFramework client) {
        return new ZkLock(client);
    }

    // Helper

    @Bean
    @SuppressWarnings("rawtypes")
    public DistributedHelper distributedHelper(List<Register> registers, List<DistributedLock> locks) {
        DistributedHelper instance = DistributedHelper.instance();

        // 添加注册中心组件
        registers.forEach(instance::addRegister);
        // 添加分布式锁组件
        locks.forEach(instance::addLock);
        // 获取所有KeyGenerator
        new ClassScanner().scan("org.s3s3l.matrix").stream().filter(c -> KeyGenerator.class.isAssignableFrom(c))
                .map(c -> {
                    try {
                        return (KeyGenerator) c.getConstructor().newInstance();
                    } catch (Exception e) {
                        return null;
                    }
                }).filter(Objects::nonNull).forEach(instance::addKeyGenerator);

        return instance;
    }
}
