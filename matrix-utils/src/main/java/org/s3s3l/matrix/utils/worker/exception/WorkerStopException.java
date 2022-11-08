package org.s3s3l.matrix.utils.worker.exception;

public class WorkerStopException extends RuntimeException {

    public WorkerStopException() {
    }

    public WorkerStopException(String message) {
        super(message);
    }

    public WorkerStopException(Throwable cause) {
        super(cause);
    }

    public WorkerStopException(String message, Throwable cause) {
        super(message, cause);
    }

    public WorkerStopException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
