package org.s3s3l.matrix.utils.bean;

import lombok.Getter;

/**
 * <p>
 * </p>
 * ClassName:KeyValuePair <br>
 * 
 * @author kehw_zwei
 * @version 1.0.0
 * @since JDK 1.8
 */
@Getter
public class KeyValuePair<K, V> {

    private final K key;
    private final V value;

    public KeyValuePair(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
