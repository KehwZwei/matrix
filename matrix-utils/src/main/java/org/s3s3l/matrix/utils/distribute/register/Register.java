package org.s3s3l.matrix.utils.distribute.register;

import org.s3s3l.matrix.utils.distribute.Listenable;
import org.s3s3l.matrix.utils.distribute.event.Event;

public interface Register<N, EN extends Enum<?>, E extends Event<EN, N>> extends Listenable<N, EN, E> {
    boolean register(String key, Object nodeInfo);

    boolean update(String key, Object nodeInfo);

    RegisterType type();
}
