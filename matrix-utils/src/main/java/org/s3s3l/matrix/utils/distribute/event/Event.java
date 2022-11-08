package org.s3s3l.matrix.utils.distribute.event;

public interface Event<E extends Enum<?>, D> {
    E eventType();

    D data();

    D oldData();

    String key();
}
