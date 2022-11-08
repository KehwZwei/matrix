package org.s3s3l.matrix.utils.bean.exception;

/**
 * <p>
 * </p>
 * 
 * @author kehw_zwei
 * @version 1.0.0
 * @since JDK 1.8
 */
public class ResourceProcessException extends RuntimeException {

    /**
     * @since JDK 1.8
     */
    private static final long serialVersionUID = -3990565237077334735L;

    public ResourceProcessException() {
        super();
    }

    public ResourceProcessException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ResourceProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceProcessException(String message) {
        super(message);
    }

    public ResourceProcessException(Throwable cause) {
        super(cause);
    }

}
