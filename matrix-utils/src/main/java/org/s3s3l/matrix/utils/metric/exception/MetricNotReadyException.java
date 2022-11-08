package org.s3s3l.matrix.utils.metric.exception;

public class MetricNotReadyException extends Exception {

    public MetricNotReadyException() {
    }

    public MetricNotReadyException(String message) {
        super(message);
    }

    public MetricNotReadyException(Throwable cause) {
        super(cause);
    }

    public MetricNotReadyException(String message, Throwable cause) {
        super(message, cause);
    }

    public MetricNotReadyException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
