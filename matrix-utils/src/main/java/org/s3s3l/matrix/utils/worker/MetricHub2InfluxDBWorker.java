package org.s3s3l.matrix.utils.worker;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.s3s3l.matrix.api.worker.Status;
import org.s3s3l.matrix.utils.influxdb.InfluxDBConfig;
import org.s3s3l.matrix.utils.influxdb.InfluxDBHelper;
import org.s3s3l.matrix.utils.metric.MetricHub;
import org.s3s3l.matrix.utils.metric.MetricHubManager;
import org.s3s3l.matrix.utils.metric.MetricMeta;
import org.s3s3l.matrix.utils.metric.Snapshot;
import org.s3s3l.matrix.utils.metric.exception.MetricNotReadyException;
import org.s3s3l.matrix.utils.worker.config.Metrics2InfluxDBWorkerConfig;

import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MetricHub2InfluxDBWorker extends DistributedWorker<Metrics2InfluxDBWorkerConfig> {
    protected final MetricHub metricHub;
    protected final InfluxDBHelper influxDBHelper;

    protected Thread workThread;

    public MetricHub2InfluxDBWorker(
            Metrics2InfluxDBWorkerConfig workerConfig, MetricHubManager metricHubManager,
            InfluxDBConfig influxDBConfig, ScheduledExecutorService scheduledExecutorService) {
        super(workerConfig);
        this.metricHub = metricHubManager.get(workerConfig.getMetricName());

        this.influxDBHelper = new InfluxDBHelper(influxDBConfig);

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if (status() != Status.RUNNING) {
                return;
            }

            try {
                Snapshot<MetricMeta> snapshot = metricHub
                        .takeSnapshotAndRemoveOlder(workerConfig.getMetricInterval() * 1000);
                if (snapshot == null) {
                    return;
                }
                List<Point> points = convertSnapshot(
                        snapshot);
                influxDBHelper.addPoints(points);
                log.info("写入到influxdb. count: {}", points.size());
            } catch (MetricNotReadyException e) {
                // ignore 等待下一轮执行
                log.debug(e.getMessage());
            } catch (Exception e) {
                log.warn("写入埋点数据到InfluxDB异常", e);
            }

        }, 5, workerConfig.getMetricInterval(), TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    protected List<Point> convertSnapshot(Snapshot<MetricMeta> snapshot) {
        return snapshot.getMetricsMap().entrySet().stream()
                .flatMap(entry -> {
                    Map<MetricMeta, Double> metricMap = entry.getValue();
                    return metricMap.entrySet().stream().map(e -> {
                        MetricMeta meta = e.getKey();
                        Double data = e.getValue();
                        String measurement = entry.getKey();
                        Point point = Point.measurement(measurement).addField("value", data).time(snapshot.getTime(),
                                WritePrecision.MS);
                        meta.getTags().entrySet().forEach(tag -> {
                            point.addTag(tag.getKey(), tag.getValue());
                        });
                        return point;
                    });
                })
                .collect(Collectors.toList());
    }

    protected String getMeasurement(String suffix) {
        return StringUtils.isEmpty(suffix) || "-".equals(StringUtils.trim(suffix)) ? workerConfig.getMeasurement()
                : String.join("-", workerConfig.getMeasurement(), suffix);
    }

    @Override
    public boolean doStart() {
        return true;
    }

    @Override
    public boolean destroyInternal() {
        try {
            influxDBHelper.close();
        } catch (Exception e) {
            log.warn("Fail to close InfluxDBHelper.", e);
        }
        log.info("Worker {} destroyed.", workerConfig.getName());

        return true;
    }

    @Override
    public Metrics2InfluxDBWorkerConfig getConfig() {
        return super.getWorkerConfig();
    }

    @Override
    protected boolean doStop() {
        return true;
    }
}
