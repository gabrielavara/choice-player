package com.gabrielavara.choiceplayer.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MessengerTest {
    private boolean messageConsumed;

    @Test
    public void testNotRegistered() {
        // when
        Messenger.send(new TestMessage());

        // then
        assertFalse(messageConsumed);
    }

    @Test
    public void testRegister() {
        // given
        Messenger.register(TestMessage.class, this::consume);

        // when
        Messenger.send(new TestMessage());

        // then
        assertTrue(messageConsumed);
    }

    private void consume(TestMessage message) {
        messageConsumed = true;
    }

    private class TestMessage {
    }
}