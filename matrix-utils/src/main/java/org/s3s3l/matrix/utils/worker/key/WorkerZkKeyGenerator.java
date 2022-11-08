package org.s3s3l.matrix.utils.worker.key;

import org.apache.curator.utils.ZKPaths;
import org.s3s3l.matrix.utils.distribute.key.KeyType;
import org.s3s3l.matrix.utils.worker.config.DistributedWorkerConfig;
import org.s3s3l.matrix.utils.zookeeper.key.ZkKeyGenerator;

public class WorkerZkKeyGenerator extends ZkKeyGenerator<DistributedWorkerConfig> {

    @Override
    protected String toPath(DistributedWorkerConfig config, KeyType type) {
        return ZKPaths.makePath("/matrix/worker", type.str(), config.getWorkType().name(), config.getName());
    }

}
