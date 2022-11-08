package org.s3s3l.matrix.utils.worker.config;

import org.s3s3l.matrix.api.worker.WorkType;
import org.s3s3l.matrix.utils.annotations.Examine;
import org.s3s3l.matrix.utils.annotations.Expectation;
import org.s3s3l.matrix.utils.worker.Worker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerConfig {
    @Examine(value = Expectation.HAS_LENGTH, msg = "请指定worker的名字")
    private String name;
    @Examine(value = Expectation.NOT_NULL, msg = "请指定workType")
    private WorkType workType;
    @SuppressWarnings("rawtypes")
    @Examine(value = Expectation.NOT_NULL, msg = "请指定worker的具体实现类")
    private Class<? extends Worker> type;
}
