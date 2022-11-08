package org.s3s3l.matrix.utils.influxdb;

import java.util.List;
import java.util.stream.Collectors;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

public class InfluxDBHelper implements AutoCloseable {

    private final InfluxDBClient client;
    private final MeasurementMapper measurementMapper = new MeasurementMapper();

    public InfluxDBHelper(InfluxDBConfig config) {
        client = InfluxDBClientFactory.create(config.getEndpoint(), config.getToken().toCharArray(), config.getOrg(),
                config.getBucket());
    }

    public <T> void add(T data) {
        WriteApiBlocking writeApi = client.getWriteApiBlocking();
        writeApi.writeMeasurement(WritePrecision.NS, data);
    }

    public <T> void add(String measurementName, T data) {
        WriteApiBlocking writeApi = client.getWriteApiBlocking();
        writeApi.writePoint(measurementMapper.toPoint(measurementName, data, WritePrecision.NS));
    }

    public <T> void multiAdd(List<T> dataList) {
        WriteApiBlocking writeApi = client.getWriteApiBlocking();
        writeApi.writeMeasurements(WritePrecision.NS, dataList);
    }

    public <T> void multiAdd(String measurementName, List<T> dataList) {
        WriteApiBlocking writeApi = client.getWriteApiBlocking();
        writeApi.writePoints(dataList.stream().map(data -> measurementMapper.toPoint(measurementName, data,
                WritePrecision.NS)).collect(Collectors.toList()));
    }

    public void addPoint(Point point) {
        WriteApiBlocking writeApi = client.getWriteApiBlocking();
        writeApi.writePoint(point);
    }

    public void addPoints(List<Point> points) {
        WriteApiBlocking writeApi = client.getWriteApiBlocking();
        writeApi.writePoints(points);
    }

    @Override
    public void close() throws Exception {
        if (client != null) {
            client.close();
        }
    }

}
