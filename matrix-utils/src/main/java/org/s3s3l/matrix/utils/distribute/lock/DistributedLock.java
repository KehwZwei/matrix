package org.s3s3l.matrix.utils.distribute.lock;

public interface DistributedLock {
    boolean tryLock(String key);

    void lock(String key);

    void unlock(String key);

    DistributedLockType type();
}
