package org.s3s3l.matrix.utils.filebeat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private String dataset;
    private String module;
    private String timezone;
}
