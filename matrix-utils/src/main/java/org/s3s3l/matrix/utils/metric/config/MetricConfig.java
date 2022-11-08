package org.s3s3l.matrix.utils.metric.config;

import java.util.ArrayList;
import java.util.List;

import org.s3s3l.matrix.utils.field.TagFieldConfig;
import org.s3s3l.matrix.utils.field.ValueFieldConfig;

import lombok.Data;

@Data
public class MetricConfig {
    private String name;
    private ValueFieldConfig valueField;
    private List<TagFieldConfig> tagFields = new ArrayList<>();
}
