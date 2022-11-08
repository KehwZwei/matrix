package org.s3s3l.matrix.utils.distribute.config;

import org.s3s3l.matrix.utils.distribute.Listenable;
import org.s3s3l.matrix.utils.distribute.event.Event;

public interface ConfigManager<C, EN extends Enum<?>, E extends Event<EN, C>> extends Listenable<C, EN, E> {
    boolean update(String key, C config);

    boolean delete(String key);
}
