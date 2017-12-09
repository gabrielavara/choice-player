package com.gabrielavara.choiceplayer.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

public class Messenger {
    private static Map<Class, List<MessageConsumer>> map = new HashMap<>();

    public static <T> void register(Class<T> clazz, MessageConsumer<T> consumer) {
        if (map.containsKey(clazz)) {
            List<MessageConsumer> consumers = map.get(clazz);
            consumers.add(consumer);
        } else {
            map.put(clazz, new ArrayList<>(singletonList(consumer)));
        }
    }

    public static <T> void send(T message) {
        if (map.containsKey(message.getClass())) {
            map.get(message.getClass()).forEach(consumer -> consumer.consume(message));
        }
    }
}
