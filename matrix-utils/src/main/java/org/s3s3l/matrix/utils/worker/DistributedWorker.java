package org.s3s3l.matrix.utils.worker;

import org.apache.commons.lang3.StringUtils;
import org.s3s3l.matrix.api.worker.Status;
import org.s3s3l.matrix.api.worker.register.NodeInfo;
import org.s3s3l.matrix.utils.bean.exception.NetException;
import org.s3s3l.matrix.utils.common.NetUtils;
import org.s3s3l.matrix.utils.distribute.DistributedHelper;
import org.s3s3l.matrix.utils.distribute.event.BasicEvent;
import org.s3s3l.matrix.utils.distribute.event.BasicEventType;
import org.s3s3l.matrix.utils.distribute.key.KeyGenerator;
import org.s3s3l.matrix.utils.distribute.key.KeyType;
import org.s3s3l.matrix.utils.distribute.listener.ListenType;
import org.s3s3l.matrix.utils.distribute.listener.Listener;
import org.s3s3l.matrix.utils.distribute.lock.DistributedLock;
import org.s3s3l.matrix.utils.distribute.register.Register;
import org.s3s3l.matrix.utils.worker.config.DistributedWorkerConfig;
import org.s3s3l.matrix.utils.worker.exception.WorkerStartupExcetpion;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class DistributedWorker<T extends DistributedWorkerConfig> extends AbstractWorker<T> {
    protected final String ip;
    protected final String key;
    protected final String currentWorkerKey;
    protected final Register<byte[], BasicEventType, BasicEvent> register;
    protected final DistributedLock distributedLock;
    /**
     * 分布式锁监听器<br>
     * 用于单例worker运行中实例停止后启动READY状态的worker进行替代
     */
    private final Listener<byte[], BasicEventType, BasicEvent> listener = (event) -> {
        switch (event.eventType()) {
            case DELETE:
                log.info("运行中实例停止");
                try {
                    Status status = status();
                    // 如果当前worker处于READY状态，则尝试启动当前worker
                    if (status == Status.READY) {
                        start();
                    }
                } catch (WorkerStartupExcetpion e) {
                    // ignore
                    log.warn("尝试启动失败：{}", e.getMessage());
                }
                break;
            default:
                break;
        }
    };

    protected DistributedWorker(T workerConfig) {
        super(workerConfig);
        ip = NetUtils.IPV4;
        if (StringUtils.isEmpty(ip)) {
            throw new NetException("未获取到主机ip");
        }
        DistributedHelper distributedHelper = DistributedHelper.instance();
        KeyGenerator<DistributedWorkerConfig> keyGenerator = distributedHelper
                .getKeyGenerator(workerConfig.getKeyGeneratorType());
        this.key = keyGenerator.getKey(workerConfig, ip, KeyType.REGISTER);
        this.currentWorkerKey = keyGenerator.getKey(workerConfig, "current", KeyType.REGISTER);
        this.register = distributedHelper.getRegister(workerConfig.getRegisterType());
        this.distributedLock = distributedHelper.getLock(workerConfig.getLockType());

        // 如果是单例的worker，注册监听器
        if (workerConfig.isSingleton()) {
            register.addListener(this.currentWorkerKey, listener, ListenType.CURRENT);
        }
    }

    @Override
    protected boolean startInternal() {
        // 如果是单例的worker，启动前先尝试锁定
        if (workerConfig.isSingleton() && !distributedLock.tryLock(this.currentWorkerKey)) {
            return false;
        }

        return doStart();
    }

    protected abstract boolean doStart();

    @Override
    protected void onStatusChange(Status newStatus) {
        // 更新注册信息
        register.update(key, new NodeInfo(ip, newStatus));
    }

    @Override
    protected boolean stopInternal() {
        boolean res = doStop();
        // 如果是单例，释放锁
        if (workerConfig.isSingleton()) {
            distributedLock.unlock(this.currentWorkerKey);
        }
        return res;
    }

    protected abstract boolean doStop();

}
