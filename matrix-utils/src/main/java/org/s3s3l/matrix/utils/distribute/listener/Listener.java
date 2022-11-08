package org.s3s3l.matrix.utils.distribute.listener;

import org.s3s3l.matrix.utils.distribute.event.Event;

public interface Listener<D, EN extends Enum<?>, E extends Event<EN, D>> {
    void onEvent(E event);
}
