package com.gabrielavara.choiceplayer.util;

import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Messenger {
    private static Map<Class, List<MessageConsumer>> map = new HashMap<>();

    private Messenger() {
    }

    public static <T> void register(Class<T> clazz, MessageConsumer<T> consumer) {
        if (map.containsKey(clazz)) {
            List<MessageConsumer> consumers = map.get(clazz);
            consumers.add(consumer);
        } else {
            map.put(clazz, new ArrayList<>(singletonList(consumer)));
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> void send(T message) {
        if (map.containsKey(message.getClass())) {
            map.get(message.getClass()).forEach(consumer -> consumer.consume(message));
        }
    }
}
