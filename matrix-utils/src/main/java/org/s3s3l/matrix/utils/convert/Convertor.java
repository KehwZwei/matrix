package org.s3s3l.matrix.utils.convert;

public interface Convertor<I,O> {
    O convert(I input);
}
