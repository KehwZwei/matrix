package org.s3s3l.matrix.utils.metric;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.s3s3l.matrix.utils.collection.CollectionUtils;
import org.s3s3l.matrix.utils.convert.ConvertorManager;
import org.s3s3l.matrix.utils.field.TagFieldConfig;
import org.s3s3l.matrix.utils.field.TimeFieldConfig;
import org.s3s3l.matrix.utils.field.ValueFieldConfig;
import org.s3s3l.matrix.utils.metric.config.MetricConfig;
import org.s3s3l.matrix.utils.metric.config.MetricCongregateType;
import org.s3s3l.matrix.utils.metric.config.MetricHubConfig;
import org.s3s3l.matrix.utils.metric.exception.MetricNotReadyException;
import org.s3s3l.matrix.utils.stuctural.jackson.JacksonUtils;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 指标交换机 <br>
 * 用于聚合指标并获取快照
 */
@Slf4j
public class MetricHub {
    /**
     * {time: slider} <br>
     * 基于开始时间排序的滑块
     */
    private final SortedMap<Long, Slider<MetricMeta>> sliderMap = new TreeMap<>();
    /**
     * 指标名称
     */
    @Getter
    private final String name;
    private final String prefix;
    private final TimeFieldConfig timeFieldConfig;
    /**
     * 每个滑块的时间跨度 ms
     */
    private final int timeRangePerSlider;
    /**
     * 最后一个指标的时间
     */
    private final AtomicLong newestMetricTime = new AtomicLong(0l);
    /**
     * 指标配置 <br>
     * {metric_name: config}
     */
    private final Map<String, MetricConfig> metricConfigMap;
    /**
     * 最后一次获取快照的时间
     */
    private long lastSnapshotTime = 0l;

    /**
     * 
     * @param timeRangePerSlider 时间窗口大小 ms
     */
    public MetricHub(MetricHubConfig config) {
        this.name = config.getName();
        this.metricConfigMap = config.getMetrics().stream()
                .collect(Collectors.toMap(c -> toCompletelyMetricName(c), Function.identity()));
        // 向下取最近的2的次方数，可以提高滑块查找效率
        this.timeRangePerSlider = tableSizeFor(config.getTimeRangePerSlider());
        this.prefix = config.getPrefix();
        this.timeFieldConfig = config.getTimeField();
    }

    /**
     * 提交指标到滑块
     * 
     * @param data
     */
    public void putMetric(JsonNode data) {
        JsonNode acturalData = JacksonUtils.get(prefix, data);
        long time;
        if (timeFieldConfig == null) {
            time = System.currentTimeMillis();
        } else {
            time = convertTime(acturalData, timeFieldConfig);
        }
        List<Metric<MetricMeta>> metrics = toMetrics(time, acturalData);
        if (CollectionUtils.isEmpty(metrics)) {
            log.warn("metric is empty, skip.");
            return;
        }
        newestMetricTime.getAndUpdate(v -> Math.max(v, time));
        getSlider(time).putMetrics(metrics);
    }

    /**
     * 
     * 获取lastSnapshotTime到lastSnapshotTime+timeRange之间的指标的快照
     * 
     * @param timeRange ms
     * @return
     * @throws MetricNotReadyException
     */
    public synchronized Snapshot<MetricMeta> takeSnapshotAndRemoveOlder(int timeRange)
            throws MetricNotReadyException {
        Snapshot<MetricMeta> snapshot = new Snapshot<>();
        if (sliderMap.isEmpty()) {
            return null;
        }

        // 如果从未获取过快照，则从第一个滑块开始
        if (lastSnapshotTime <= 0) {
            lastSnapshotTime = sliderMap.firstKey();
        }
        long start = lastSnapshotTime;
        long current = start;
        long end = current + timeRange;

        // 如果获取的区间大于最后一个指标的时间，则快照失败，需等待区间内的指标收集完成
        if (end > newestMetricTime.get()) {
            throw new MetricNotReadyException("指标还未完成收集，time： " + end);
        }

        // 设置快照时间
        snapshot.setTime(lastSnapshotTime);

        log.info("snapshottime: {}, {}", lastSnapshotTime, snapshot.getTime());

        Map<String, Map<MetricMeta, Double>> metricsMap = new HashMap<>();

        Map<String, Map<MetricMeta, MetricData>> metricsCacheMap = new HashMap<>();

        // 依次获取快照时间区间内的所有滑块，并对滑块内的指标进行聚合
        while (current <= end) {
            Slider<MetricMeta> slider = sliderMap.get(current);
            current += timeRangePerSlider;
            if (slider == null) {
                continue;
            }

            // 获取滑块的指标快照，并进行聚合
            Map<String, Map<MetricMeta, MetricData>> sliderSnapshot = slider.takeSnapshot();
            for (Entry<String, Map<MetricMeta, MetricData>> entry : sliderSnapshot.entrySet()) {
                for (Entry<MetricMeta, MetricData> mEntry : entry.getValue().entrySet()) {
                    String metricName = entry.getKey();
                    MetricMeta metricMeta = mEntry.getKey();
                    MetricCongregateType congregateType = metricConfigMap.get(metricName).getValueField()
                            .getCongregateType();
                    metricsCacheMap.computeIfAbsent(metricName, key -> new HashMap<>()).compute(metricMeta,
                            (k, v) -> {
                                MetricData metricData = mEntry.getValue();
                                MetricData newData = v;
                                if (newData == null) {
                                    newData = metricData;
                                } else {
                                    newData = newData.append(metricData);
                                }

                                metricsMap.computeIfAbsent(metricName, key -> new HashMap<>()).put(metricMeta,
                                        newData.get(congregateType));

                                return newData;
                            });
                }
            }
        }
        lastSnapshotTime = current;

        // 清理已经执行过快照的滑块
        Set<Long> oldKeys = new HashSet<>(sliderMap.headMap(current).keySet());
        for (Long oldKey : oldKeys) {
            sliderMap.remove(oldKey);
            log.info("remove: {}", oldKey);
        }

        snapshot.setMetricsMap(metricsMap);
        return snapshot;
    }

    private synchronized Slider<MetricMeta> getSlider(long time) {
        return sliderMap.computeIfAbsent(cell(time), key -> new Slider<>());
    }

    private List<Metric<MetricMeta>> toMetrics(long time, JsonNode data) {
        return metricConfigMap.values().stream().map(config -> {
            Metric<MetricMeta> m = new Metric<>();
            Double value = convertValue(data, config.getValueField());
            MetricMeta meta = new MetricMeta();
            for (TagFieldConfig tagFieldConfig : config.getTagFields()) {
                meta.addTag(tagFieldConfig.getTargetFieldName(), convertTag(data, tagFieldConfig));
            }
            m.setName(toCompletelyMetricName(config));
            m.setTime(time);
            m.setValue(value);
            m.setMeta(meta);
            return m;
        }).collect(Collectors.toList());
    }

    private String toCompletelyMetricName(MetricConfig config) {
        return String.join("_", this.name, config.getName());
    }

    private Double convertValue(JsonNode metric, ValueFieldConfig config) {
        return ConvertorManager
                .getConvertor(config.getConvertor())
                .convert(JacksonUtils.getNumber(config.getSourceFieldName(), metric));
    }

    private String convertTag(JsonNode metric, TagFieldConfig config) {
        return ConvertorManager
                .getConvertor(config.getConvertor())
                .convert(JacksonUtils.getString(config.getSourceFieldName(), metric));
    }

    private long convertTime(JsonNode metric, TimeFieldConfig config) {
        return ConvertorManager
                .getConvertor(config.getConvertor())
                .convert(JacksonUtils.getString(config.getSourceFieldName(), metric));
    }

    // 去除余数
    private long cell(long time) {
        int base = timeRangePerSlider - 1;
        return (time | base) ^ base;
    }

    /**
     * @see java.util.HashMap#tableSizeFor(int)
     */
    private int tableSizeFor(int var0) {
        int var1 = var0 - 1;
        var1 |= var1 >>> 1;
        var1 |= var1 >>> 2;
        var1 |= var1 >>> 4;
        var1 |= var1 >>> 8;
        var1 |= var1 >>> 16;
        return var1 < 0 ? 1 : (var1 >= 1073741824 ? 1073741824 : var1 + 1);
    }
}
