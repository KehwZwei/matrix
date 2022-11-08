package org.s3s3l.matrix.utils.convert;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.s3s3l.matrix.utils.convert.exception.ConvertorException;

@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class ConvertorManager {
    private static final Map<Class<? extends Convertor>, Convertor> convertors = new ConcurrentHashMap<>();

    public static <I, O> Convertor<I, O> getConvertor(Class<? extends Convertor<I, O>> type) {
        return convertors.computeIfAbsent(type, k -> {
            try {
                return k.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                throw new ConvertorException(e);
            }
        });
    }
}
