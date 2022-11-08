package org.s3s3l.matrix.utils.distribute.key;

public interface KeyGenerator<T> {
    String getKey(T config, String id, KeyType type);
}
