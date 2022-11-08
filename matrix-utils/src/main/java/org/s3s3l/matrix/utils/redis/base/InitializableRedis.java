package org.s3s3l.matrix.utils.redis.base;

public interface InitializableRedis<T> extends IRedis {
    
    void init(T configuration);
}
