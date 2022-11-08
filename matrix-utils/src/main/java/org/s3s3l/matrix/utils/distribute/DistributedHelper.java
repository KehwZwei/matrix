package org.s3s3l.matrix.utils.distribute;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.s3s3l.matrix.utils.distribute.key.KeyGenerator;
import org.s3s3l.matrix.utils.distribute.lock.DistributedLock;
import org.s3s3l.matrix.utils.distribute.lock.DistributedLockType;
import org.s3s3l.matrix.utils.distribute.register.Register;
import org.s3s3l.matrix.utils.distribute.register.RegisterType;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DistributedHelper {
    private static final DistributedHelper INSTANCE = new DistributedHelper();

    public static DistributedHelper instance() {
        return INSTANCE;
    }

    private DistributedHelper() {
        
    }

    private final Map<DistributedLockType, DistributedLock> lockCache = new ConcurrentHashMap<>();

    private final Map<RegisterType, Register> registerCache = new ConcurrentHashMap<>();

    private final Map<Class<?>, KeyGenerator> keyGeneratorCache = new ConcurrentHashMap<>();

    public void addLock(DistributedLock lock) {
        lockCache.compute(lock.type(), (key, oldValue) -> {
            if (oldValue != null) {
                throw new OperationException("lock of type " + lock.type() + " alreay exist.");
            }

            return lock;
        });
    }

    public void removeLock(DistributedLockType type) {
        lockCache.remove(type);
    }

    public DistributedLock getLock(DistributedLockType type) {
        return lockCache.get(type);
    }

    public void addRegister(Register register) {
        registerCache.compute(register.type(), (key, oldValue) -> {
            if (oldValue != null) {
                throw new OperationException("register of type " + register.type() + " alreay exist.");
            }

            return register;
        });
    }

    public void removeRegister(RegisterType registerType) {
        registerCache.remove(registerType);
    }

    public <T extends Register> T getRegister(RegisterType type) {
        return (T) registerCache.get(type);
    }

    public void addKeyGenerator(KeyGenerator keyGenerator) {
        Class<?> type = keyGenerator.getClass();
        keyGeneratorCache.compute(type, (key, oldValue) -> {
            if (oldValue != null) {
                throw new OperationException("register of type " + type.getName() + " alreay exist.");
            }

            return keyGenerator;
        });
    }

    public <T extends KeyGenerator> T getKeyGenerator(Class<T> type) {
        return (T) keyGeneratorCache.get(type);
    }

    public static class OperationException extends RuntimeException {

        public OperationException() {
        }

        public OperationException(String arg0) {
            super(arg0);
        }

        public OperationException(Throwable arg0) {
            super(arg0);
        }

        public OperationException(String arg0, Throwable arg1) {
            super(arg0, arg1);
        }

        public OperationException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
            super(arg0, arg1, arg2, arg3);
        }

    }
}
