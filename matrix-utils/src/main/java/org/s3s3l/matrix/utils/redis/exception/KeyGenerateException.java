package org.s3s3l.matrix.utils.redis.exception;

public class KeyGenerateException extends RuntimeException {

    /**
     * @since JDK 1.8
     */
    private static final long serialVersionUID = 8611443895189876328L;

    public KeyGenerateException() {
        super();
    }

    public KeyGenerateException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public KeyGenerateException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyGenerateException(String message) {
        super(message);
    }

    public KeyGenerateException(Throwable cause) {
        super(cause);
    }

}
