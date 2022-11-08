package org.s3s3l.matrix.utils.zookeeper.listener;

import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ZkListenerMeta {
    private CuratorCacheListener curatorCacheListener;
    private CuratorCache curatorCache;
}
