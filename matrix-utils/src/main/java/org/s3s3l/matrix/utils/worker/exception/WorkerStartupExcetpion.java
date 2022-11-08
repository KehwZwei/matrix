package org.s3s3l.matrix.utils.worker.exception;

public class WorkerStartupExcetpion extends RuntimeException {

    public WorkerStartupExcetpion() {
    }

    public WorkerStartupExcetpion(String message) {
        super(message);
    }

    public WorkerStartupExcetpion(Throwable cause) {
        super(cause);
    }

    public WorkerStartupExcetpion(String message, Throwable cause) {
        super(message, cause);
    }

    public WorkerStartupExcetpion(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
