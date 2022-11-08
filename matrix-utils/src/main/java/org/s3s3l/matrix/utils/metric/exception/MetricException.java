package org.s3s3l.matrix.utils.metric.exception;

public class MetricException extends RuntimeException {

    public MetricException() {
    }

    public MetricException(String message) {
        super(message);
    }

    public MetricException(Throwable cause) {
        super(cause);
    }

    public MetricException(String message, Throwable cause) {
        super(message, cause);
    }

    public MetricException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
