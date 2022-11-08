package org.s3s3l.matrix.utils.worker;

import java.util.concurrent.atomic.AtomicReference;

import org.s3s3l.matrix.api.worker.Status;
import org.s3s3l.matrix.utils.common.StringUtils;
import org.s3s3l.matrix.utils.worker.config.WorkerConfig;
import org.s3s3l.matrix.utils.worker.exception.WorkerStartupExcetpion;
import org.s3s3l.matrix.utils.worker.exception.WorkerStopException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractWorker<T extends WorkerConfig> implements Worker<T> {
    /**
     * 操作锁，start|stop|destroy，都进行锁定，防止并发
     */
    private final Object operationLock = new Object();
    private final AtomicReference<Status> status = new AtomicReference<>(Status.INIT);
    @Getter
    protected final T workerConfig;
    @Getter
    private final String id;

    protected AbstractWorker(T workerConfig) {
        this.workerConfig = workerConfig;
        this.id = StringUtils.getUUIDNoLine();
    }

    @Override
    public void start() {
        synchronized (operationLock) {
            if (!changeStatus(Status.INIT, Status.STARTING) && !changeStatus(Status.READY,
                    Status.STARTING) && !changeStatus(Status.STOPED, Status.STARTING)) {
                throw new WorkerStartupExcetpion("当前状态不支持启动: " + status.get());
            }

            log.info("Worker： 启动中...", workerConfig.getName());

            if (startInternal()) {
                finishStart();
            } else {
                startFail();
            }
        }
    }

    /**
     * start()操作中调用
     * 
     * @return
     */
    protected abstract boolean startInternal();

    private void startFail() {
        changeStatus(Status.STARTING, Status.READY);
    }

    private void finishStart() {
        if (!changeStatus(Status.STARTING, Status.RUNNING)) {
            throw new WorkerStartupExcetpion("启动失败: " + status.get());
        }

        log.info("Worker： 启动成功.", workerConfig.getName());
    }

    @Override
    public void stop() {
        synchronized (operationLock) {
            if (!changeStatus(Status.RUNNING, Status.STOPED)) {
                throw new WorkerStopException("当前状态不支持停止: " + status.get());
            }

            stopInternal();

            log.info("Worker： 已停止.", workerConfig.getName());
        }
    }

    /**
     * stop()操作中调用
     * 
     * @return
     */
    protected abstract boolean stopInternal();

    @Override
    public void destroy() {
        synchronized (operationLock) {
            if (!changeStatus(Status.STOPED, Status.DESTROYED)) {
                throw new WorkerStopException("当前状态不支持销毁，请先停止: " + status.get());
            }
            destroyInternal();
        }
    }

    /**
     * destroy()操作中调用
     * 
     * @return
     */
    protected boolean destroyInternal() {
        return true;
    }

    @Override
    public Status status() {
        return status.get();
    }

    private boolean changeStatus(Status expect, Status update) {
        boolean res = status.compareAndSet(expect, update);
        if (res) {
            onStatusChange(update);
        }

        return res;
    }

    /**
     * 状态变更时触发
     * 
     * @param newStatus
     */
    protected void onStatusChange(Status newStatus) {
    }
}
