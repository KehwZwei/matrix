package org.s3s3l.matrix.utils.worker.assembler;

import org.s3s3l.matrix.api.worker.WorkerDTO;
import org.s3s3l.matrix.utils.worker.Worker;
import org.s3s3l.matrix.utils.worker.config.WorkerConfig;

public abstract class WorkerAssembler {
    public static WorkerDTO toDto(Worker<?> worker) {
        WorkerConfig workerConfig = worker.getConfig();
        return WorkerDTO.builder()
                .id(worker.getId())
                .name(workerConfig.getName())
                .workType(worker.getConfig().getWorkType())
                .type(worker.getConfig().getType().getName())
                .status(worker.status())
                .build();
    }
}
