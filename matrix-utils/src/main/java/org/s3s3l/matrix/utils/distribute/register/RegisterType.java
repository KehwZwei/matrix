package org.s3s3l.matrix.utils.distribute.register;

import org.s3s3l.matrix.utils.zookeeper.register.ZkRegister;

@SuppressWarnings("rawtypes")
public enum RegisterType {
    ZK(ZkRegister.class);

    private final Class<? extends Register> type;

    private RegisterType(Class<? extends Register> type) {
        this.type = type;
    }

    public Class<?> type() {
        return this.type;
    }
}
