package org.s3s3l.matrix.utils.metric;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.s3s3l.matrix.utils.verify.Verify;

/**
 * 滑块 <br>
 * 收集指定时间窗口内的指标数据，并进行聚合
 */
public class Slider<T extends Comparable<T>> {
    /**
     * {metric_name: {meta: data}}
     */
    private final Map<String, Map<T, AtomicReference<MetricData>>> metricsMap = new ConcurrentHashMap<>();

    /**
     * 批量提交指标
     * 
     * @param metrics
     */
    public void putMetrics(Collection<Metric<T>> metrics) {
        metrics.forEach(this::putMetricNoLock);
    }

    /**
     * 提交指标
     * 
     * @param metric
     */
    public void putMetric(Metric<T> metric) {
        putMetricNoLock(metric);
    }

    /**
     * 提交指标（无锁）
     * 
     * @param metric
     */
    private void putMetricNoLock(Metric<T> metric) {
        Verify.notNull(metric);

        metricsMap.computeIfAbsent(metric.getName(), key -> new ConcurrentHashMap<>())
                .computeIfAbsent(
                        metric.getMeta(), key -> new AtomicReference<>(new MetricData(0d)))
                .updateAndGet(old -> old.append(metric.getValue()));
    }

    /**
     * 获取快照<br>
     * 获取快照时阻塞当前滑块的所有指标提交
     * 
     * @return
     */
    public Map<String, Map<T, MetricData>> takeSnapshot() {
        Map<String, Map<T, MetricData>> snapshot = new HashMap<>();
        for (Entry<String, Map<T, AtomicReference<MetricData>>> entry : metricsMap.entrySet()) {
            for (Entry<T, AtomicReference<MetricData>> mEntry : entry.getValue().entrySet()) {
                snapshot.computeIfAbsent(entry.getKey(), key -> new HashMap<>()).put(mEntry.getKey(),
                        mEntry.getValue().get());
            }
        }

        return snapshot;
    }

}
