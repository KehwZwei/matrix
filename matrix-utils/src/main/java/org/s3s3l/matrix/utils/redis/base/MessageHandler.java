package org.s3s3l.matrix.utils.redis.base;

@FunctionalInterface
public interface MessageHandler<T> {
    void handle(T message, String pattern);
}
