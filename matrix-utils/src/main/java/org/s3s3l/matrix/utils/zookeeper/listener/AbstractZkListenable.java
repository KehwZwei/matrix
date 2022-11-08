package org.s3s3l.matrix.utils.zookeeper.listener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCache.Options;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.s3s3l.matrix.utils.distribute.Listenable;
import org.s3s3l.matrix.utils.distribute.event.BasicEvent;
import org.s3s3l.matrix.utils.distribute.event.BasicEventType;
import org.s3s3l.matrix.utils.distribute.listener.ListenType;
import org.s3s3l.matrix.utils.distribute.listener.Listener;
import org.s3s3l.matrix.utils.distribute.register.exception.ListenerRegisterException;

public abstract class AbstractZkListenable implements Listenable<byte[], BasicEventType, BasicEvent> {
    protected final Map<ListenType, Map<String, CuratorCache>> cacheMap = new ConcurrentHashMap<>();
    protected final Map<Listener<byte[], BasicEventType, BasicEvent>, ZkListenerMeta> listenerCache = new ConcurrentHashMap<>();
    protected final CuratorFramework client;

    public AbstractZkListenable(CuratorFramework client) {
        this.client = client;
    }

    @Override
    public void addListener(String key, Listener<byte[], BasicEventType, BasicEvent> listener, ListenType type) {

        if (listenerCache.containsKey(listener)) {
            throw new ListenerRegisterException("Listener was registed.");
        }

        CuratorCacheListener curatorCacheListener = CuratorCacheListener.builder()
                .forInitialized(() -> {
                    listener.onEvent(BasicEvent.builder().eventType(BasicEventType.INITIALIZED).build());
                })
                .forCreates(
                        data -> listener.onEvent(
                                BasicEvent.builder().key(data.getPath()).eventType(BasicEventType.CREATE)
                                        .data(() -> data.getData())
                                        .build()))
                .forChanges((oldData,
                        newData) -> listener
                                .onEvent(BasicEvent.builder().key(newData.getPath()).eventType(BasicEventType.CHANGE)
                                        .oldData(() -> oldData.getData())
                                        .data(() -> newData
                                                .getData())
                                        .build()))
                .forDeletes(data -> listener
                        .onEvent(BasicEvent.builder().key(data.getPath()).eventType(BasicEventType.DELETE)
                                .data(() -> data
                                        .getData())
                                .build()))
                .build();

        CuratorCache curatorCache = cacheMap.computeIfAbsent(type, t -> new ConcurrentHashMap<>()).computeIfAbsent(key,
                k -> {
                    CuratorCache cache;
                    switch (type) {
                        case TREE:
                            cache = CuratorCache.build(client, key);
                            break;
                        case CHILDREN:
                            cache = CuratorCache.build(client, key);
                            break;
                        case CURRENT:
                        default:
                            cache = CuratorCache.build(client, key, Options.SINGLE_NODE_CACHE);
                            break;
                    }
                    return cache;
                });
        curatorCache.listenable().addListener(curatorCacheListener);
        curatorCache.start();

        listenerCache.put(listener,
                ZkListenerMeta.builder().curatorCache(curatorCache).curatorCacheListener(curatorCacheListener).build());
    }

    @Override
    public void removeListener(Listener<byte[], BasicEventType, BasicEvent> listener) {
        listenerCache.computeIfPresent(listener, (k, meta) -> {
            meta.getCuratorCache().listenable()
                    .removeListener(meta.getCuratorCacheListener());
            return null;
        });
    }
}
