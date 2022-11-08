package org.s3s3l.matrix.utils.metric;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Metric<T extends Comparable<T>> {
    /**
     * 指标名称, {hub_name}_{metric_name}
     */
    private String name;
    private long time;
    private T meta;
    private double value;
}
