package org.s3s3l.matrix.utils.field;

import org.s3s3l.matrix.utils.convert.Convertor;
import org.s3s3l.matrix.utils.convert.TimeConvertor;

import lombok.Data;

@Data
public class TimeFieldConfig extends FieldConfig {
    private Class<? extends Convertor<String, Long>> convertor = TimeConvertor.class;
}
