package org.s3s3l.matrix.api.worker.condition;

import org.s3s3l.matrix.api.worker.WorkType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerCondition {
    private String id;
    private WorkType workType;
}
