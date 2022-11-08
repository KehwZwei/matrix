package org.s3s3l.matrix.utils.verify;

/**
 * <p>
 * </p>
 * ClassName:Verifier <br>
 * 
 * @author kehw_zwei
 * @version 1.0.0
 * @since JDK 1.8
 */
public interface Verifier {

    void tryVerify(Object param, Class<?> type);

    <T> void verify(T param, Class<T> type);

    <T> void verify(T param, Class<T> type, String scope);
}
