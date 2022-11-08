package org.s3s3l.matrix.api.worker.register;

import org.s3s3l.matrix.api.worker.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class NodeInfo {
    private String ip;
    private Status status;
}
