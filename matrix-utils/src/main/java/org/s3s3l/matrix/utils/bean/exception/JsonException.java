package org.s3s3l.matrix.utils.bean.exception;

/**
 * <p>
 * </p>
 * ClassName:JsonException <br>
 * 
 * @author kehw_zwei
 * @version 1.0.0
 * @since JDK 1.8
 */
public class JsonException extends RuntimeException {

    /**
     * @since JDK 1.8
     */
    private static final long serialVersionUID = -4197522765535634504L;

    public JsonException() {
        super();
    }

    public JsonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonException(String message) {
        super(message);
    }

    public JsonException(Throwable cause) {
        super(cause);
    }

}
