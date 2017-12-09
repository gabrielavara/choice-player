package com.gabrielavara.choiceplayer.util;

@FunctionalInterface
public interface MessageConsumer<T> {
    void consume(T message);
}
