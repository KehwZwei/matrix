package org.s3s3l.matrix.utils.worker.config;

import org.s3s3l.matrix.utils.distribute.key.KeyGenerator;
import org.s3s3l.matrix.utils.distribute.lock.DistributedLockType;
import org.s3s3l.matrix.utils.distribute.register.RegisterType;
import org.s3s3l.matrix.utils.worker.key.WorkerZkKeyGenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DistributedWorkerConfig extends WorkerConfig {
    @Builder.Default
    private boolean singleton = false;
    @Builder.Default
    private DistributedLockType lockType = DistributedLockType.ZK;
    @Builder.Default
    private RegisterType registerType = RegisterType.ZK;
    @Builder.Default
    private Class<? extends KeyGenerator<DistributedWorkerConfig>> keyGeneratorType = WorkerZkKeyGenerator.class;

}
