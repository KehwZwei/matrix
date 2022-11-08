package org.s3s3l.matrix.utils.influxdb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class InfluxDBConfig {
    private String endpoint;
    private String org;
    private String bucket;
    private String token;
}
