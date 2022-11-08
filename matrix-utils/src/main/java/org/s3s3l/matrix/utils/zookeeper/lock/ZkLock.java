package org.s3s3l.matrix.utils.zookeeper.lock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.s3s3l.matrix.utils.distribute.lock.DistributedLock;
import org.s3s3l.matrix.utils.distribute.lock.DistributedLockType;
import org.s3s3l.matrix.utils.distribute.lock.LockAcquireException;

public class ZkLock implements DistributedLock {

    private final CuratorFramework client;
    private final Map<String, InterProcessMutex> lockCache = new ConcurrentHashMap<>();

    public ZkLock(CuratorFramework client) {
        this.client = client;
    }

    @Override
    public boolean tryLock(String key) {
        try {
            return getLock(key).acquire(0, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new LockAcquireException(e);
        }
    }

    @Override
    public void lock(String key) {
        try {
            getLock(key).acquire();
        } catch (Exception e) {
            throw new LockAcquireException(e);
        }
    }

    @Override
    public void unlock(String key) {
        try {
            getLock(key).release();
        } catch (Exception e) {
            throw new LockAcquireException(e);
        }
    }

    private InterProcessMutex getLock(String key) {
        return lockCache.computeIfAbsent(key, k -> new InterProcessMutex(client, key));
    }

    @Override
    public DistributedLockType type() {
        return DistributedLockType.ZK;
    }

}
