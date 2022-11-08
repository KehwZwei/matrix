package org.s3s3l.matrix.utils.redis.exception;

public class RedisExcuteException extends RuntimeException {

    /**
     * @since JDK 1.8
     */
    private static final long serialVersionUID = 8611443895189876328L;

    public RedisExcuteException() {
        super();
    }

    public RedisExcuteException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public RedisExcuteException(String message, Throwable cause) {
        super(message, cause);
    }

    public RedisExcuteException(String message) {
        super(message);
    }

    public RedisExcuteException(Throwable cause) {
        super(cause);
    }

}
