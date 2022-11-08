package org.s3s3l.matrix.utils.field;

import org.s3s3l.matrix.utils.convert.Convertor;
import org.s3s3l.matrix.utils.convert.IdentityConvertor;

import lombok.Data;

@Data
public class TagFieldConfig extends FieldConfig {
    private String targetFieldName;
    private Class<? extends Convertor<String, String>> convertor = IdentityConvertor.class;
}
