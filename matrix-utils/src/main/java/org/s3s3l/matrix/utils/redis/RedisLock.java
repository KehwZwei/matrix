package org.s3s3l.matrix.utils.redis;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.s3s3l.matrix.utils.distribute.lock.LockAcquireException;
import org.s3s3l.matrix.utils.redis.base.IRedis;

import lombok.AllArgsConstructor;
import redis.clients.jedis.params.SetParams;

@AllArgsConstructor
public class RedisLock implements Lock {
    private final IRedis redis;
    private final String key;

    @Override
    public void lock() {
        if (!IRedis.isSuccess(redis.set(key, "", new SetParams().nx()))) {
            throw new LockAcquireException("锁'" + key + "'已被占用");
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        while (true) {
            if (tryLock()) {
                return;
            }
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public Condition newCondition() {
        throw new RuntimeException("function not supported");
    }

    @Override
    public boolean tryLock() {
        return IRedis.isSuccess(redis.set(key, "", new SetParams().nx()));
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        LocalDateTime failTime = LocalDateTime.now().plus(time, IRedis.toChronoUnit(unit));
        while (LocalDateTime.now().isBefore(failTime)) {
            if (tryLock()) {
                return true;
            }
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return false;
    }

    @Override
    public void unlock() {
        redis.del(key);
    }
    
}
