package com.gabrielavara.choiceplayer.messenger;

@FunctionalInterface
public interface MessageConsumer<T> {
    void consume(T message);
}
