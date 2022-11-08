package org.s3s3l.matrix.utils.filebeat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Metadata {
    private String beat;
    private String type;
    private String version;
    private String pipeline;
}
