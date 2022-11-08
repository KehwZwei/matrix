package org.s3s3l.matrix.utils.field;

import org.s3s3l.matrix.utils.convert.Convertor;
import org.s3s3l.matrix.utils.convert.NumberToDoubleConvertor;
import org.s3s3l.matrix.utils.metric.config.MetricCongregateType;

import lombok.Data;

@Data
public class ValueFieldConfig extends FieldConfig {
    private MetricCongregateType congregateType = MetricCongregateType.LAST;
    private Class<? extends Convertor<Number, Double>> convertor = NumberToDoubleConvertor.class;
}
