package org.s3s3l.matrix.utils.zookeeper.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.s3s3l.matrix.utils.distribute.config.ConfigManager;
import org.s3s3l.matrix.utils.distribute.config.exception.ConfigException;
import org.s3s3l.matrix.utils.distribute.event.BasicEvent;
import org.s3s3l.matrix.utils.distribute.event.BasicEventType;
import org.s3s3l.matrix.utils.zookeeper.listener.AbstractZkListenable;

public class ZkConfigManager extends AbstractZkListenable implements ConfigManager<byte[], BasicEventType, BasicEvent> {

    public ZkConfigManager(CuratorFramework client) {
        super(client);
    }

    @Override
    public boolean update(String key, byte[] config) {
        try {
            client.create().orSetData().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(key,
                    config);
        } catch (Exception e) {
            throw new ConfigException(e);
        }
        return true;
    }

    @Override
    public boolean delete(String key) {
        try {
            client.delete().deletingChildrenIfNeeded().forPath(key);
        } catch (Exception e) {
            throw new ConfigException(e);
        }
        return true;
    }
}
