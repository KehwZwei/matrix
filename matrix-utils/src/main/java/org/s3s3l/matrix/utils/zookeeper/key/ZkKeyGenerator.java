package org.s3s3l.matrix.utils.zookeeper.key;

import org.apache.curator.utils.ZKPaths;
import org.s3s3l.matrix.utils.distribute.key.KeyGenerator;
import org.s3s3l.matrix.utils.distribute.key.KeyType;

public abstract class ZkKeyGenerator<T> implements KeyGenerator<T> {

    @Override
    public String getKey(T config, String id, KeyType type) {
        return ZKPaths.makePath(toPath(config, type), id);
    }

    protected abstract String toPath(T config, KeyType type);
    
}
