package com.gabrielavara.choiceplayer.util;

import javafx.util.Duration;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TimeFormatterTest {
    @Test
    public void getFormattedTimes() {
        // given
        Duration elapsed = Duration.minutes(1).add(Duration.seconds(2));
        Duration duration = Duration.minutes(3).add(Duration.seconds(4));

        // when
        TimeFormatter.Times formattedTimes = TimeFormatter.getFormattedTimes(elapsed, duration);

        // then
        assertEquals("1:02", formattedTimes.getElapsed());
        assertEquals("2:02", formattedTimes.getRemaining());
    }

    @Test
    public void getFormattedLength() {
        // given
        int seconds = (int) Duration.minutes(3).add(Duration.seconds(4)).toSeconds();

        // when
        String length = TimeFormatter.getFormattedLength(seconds);

        // then
        assertEquals("3:04", length);
    }

}