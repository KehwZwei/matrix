package org.s3s3l.matrix.utils.metric;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Snapshot<T> {
    private long time;
    /**
     * {metric_name: {meta: value}}
     */
    private Map<String, Map<T, Double>> metricsMap;
}
