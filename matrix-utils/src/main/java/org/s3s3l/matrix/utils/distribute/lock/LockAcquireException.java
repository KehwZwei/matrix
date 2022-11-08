package org.s3s3l.matrix.utils.distribute.lock;

public class LockAcquireException extends RuntimeException {

    public LockAcquireException() {
    }

    public LockAcquireException(String message) {
        super(message);
    }

    public LockAcquireException(Throwable cause) {
        super(cause);
    }

    public LockAcquireException(String message, Throwable cause) {
        super(message, cause);
    }

    public LockAcquireException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
