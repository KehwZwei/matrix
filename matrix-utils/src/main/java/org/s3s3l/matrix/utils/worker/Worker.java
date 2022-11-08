package org.s3s3l.matrix.utils.worker;

import org.s3s3l.matrix.api.worker.Status;
import org.s3s3l.matrix.utils.worker.config.WorkerConfig;

public interface Worker<T extends WorkerConfig> {
    void start();

    Status status();

    void stop();

    void destroy();

    T getConfig();

    String getId();
}
