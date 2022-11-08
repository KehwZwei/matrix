package org.s3s3l.matrix.utils.metric;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.s3s3l.matrix.utils.metric.config.MetricHubConfig;
import org.s3s3l.matrix.utils.metric.exception.MetricException;

public class MetricHubManager {
    private Map<String, MetricHub> metricHubs = new ConcurrentHashMap<>();
    private final Map<String, MetricHubConfig> configs;

    public MetricHubManager(List<MetricHubConfig> configs) {
        this.configs = configs.stream().collect(Collectors.toMap(MetricHubConfig::getName, Function.identity()));
    }

    public MetricHubConfig getConfig(String name) {
        return configs.get(name);
    }

    public MetricHub get(String name) {
        if (!configs.containsKey(name)) {
            throw new MetricException("Config not found for MetricHub: " + name);
        }
        return metricHubs.computeIfAbsent(name, key -> new MetricHub(configs.get(key)));
    }
}
