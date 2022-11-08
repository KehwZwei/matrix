package org.s3s3l.matrix.utils.zookeeper.register;

import javax.annotation.PreDestroy;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.s3s3l.matrix.utils.distribute.event.BasicEvent;
import org.s3s3l.matrix.utils.distribute.event.BasicEventType;
import org.s3s3l.matrix.utils.distribute.listener.ListenType;
import org.s3s3l.matrix.utils.distribute.register.Register;
import org.s3s3l.matrix.utils.distribute.register.RegisterType;
import org.s3s3l.matrix.utils.distribute.register.exception.RegisterException;
import org.s3s3l.matrix.utils.stuctural.jackson.JacksonUtils;
import org.s3s3l.matrix.utils.zookeeper.listener.AbstractZkListenable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

public class ZkRegister extends AbstractZkListenable implements Register<byte[], BasicEventType, BasicEvent> {

    public ZkRegister(CuratorFramework client) {
        super(client);
    }

    @Override
    public boolean register(String key, Object nodeInfo) {
        try {
            client.create().orSetData().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(key,
                    JacksonUtils.NON_NULL.toStructuralBytes(nodeInfo));
        } catch (Exception e) {
            throw new RegisterException(e);
        }
        return true;
    }

    @Override
    public boolean update(String key, Object nodeInfo) {
        try {
            client.create().orSetData().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(key,
                    JacksonUtils.NON_NULL.toStructuralBytes(nodeInfo));
        } catch (Exception e) {
            throw new RegisterException(e);
        }
        return true;
    }

    @PreDestroy
    public void destroy() {
        cacheMap.values().stream().flatMap(m -> m.values().stream()).forEach(CuratorCache::close);
    }

    @Override
    public RegisterType type() {
        return RegisterType.ZK;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NodeInfo {
        private String ip;
    }

    public static void main(String[] args) throws InterruptedException {
        CuratorFramework client = CuratorFrameworkFactory.newClient(
                "121.37.137.172:2181,123.60.111.121:2181,123.60.86.137:2181",
                new RetryNTimes(3, 100));

        client.start();

        Register<byte[], BasicEventType, BasicEvent> register = new ZkRegister(client);

        register.addListener("/test/register", (event) -> {
            System.out.println(event.key() + " " + event.eventType());
        }, ListenType.TREE);

        register.register("/test/register/testNode", new NodeInfo("test-ip"));
    }

}
