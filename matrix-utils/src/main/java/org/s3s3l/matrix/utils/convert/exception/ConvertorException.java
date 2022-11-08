package org.s3s3l.matrix.utils.convert.exception;

public class ConvertorException extends RuntimeException {

    public ConvertorException() {
    }

    public ConvertorException(String message) {
        super(message);
    }

    public ConvertorException(Throwable cause) {
        super(cause);
    }

    public ConvertorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConvertorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
