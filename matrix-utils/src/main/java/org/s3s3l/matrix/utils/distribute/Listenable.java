package org.s3s3l.matrix.utils.distribute;

import org.s3s3l.matrix.utils.distribute.event.Event;
import org.s3s3l.matrix.utils.distribute.listener.ListenType;
import org.s3s3l.matrix.utils.distribute.listener.Listener;

public interface Listenable<N, EN extends Enum<?>, E extends Event<EN, N>> {
    void addListener(String key, Listener<N, EN, E> listener, ListenType type);

    void removeListener(Listener<N, EN, E> listener);
}
