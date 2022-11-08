package org.s3s3l.matrix.api.worker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerDTO {
    private String id;
    private String name;
    private WorkType workType;
    private String type;
    private Status status;
}
