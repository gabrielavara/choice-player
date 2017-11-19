package com.gabrielavara.choiceplayer.util;

import javafx.util.Duration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

public class TimeFormatter {

    private static final String ELAPSED_TEMPLATE_WITH_HOURS = "%d:%02d:%02d";
    private static final String ELAPSED_TEMPLATE = "%d:%02d";

    private TimeFormatter() {
    }

    public static Times getFormattedTimes(Duration elapsed, Duration duration) {
        Duration remaining = duration.subtract(elapsed);
        return new Times(getFormattedDuration(elapsed), getFormattedDuration(remaining));
    }

    private static String getFormattedDuration(Duration duration) {
        return formatTime(getTime(duration));
    }

    public static String getFormattedLength(int seconds) {
        return formatTime(getTime(seconds));
    }

    private static Time getTime(Duration duration) {
        int seconds = (int) Math.floor(duration.toSeconds());
        return getTime(seconds);
    }

    private static Time getTime(int seconds) {
        int elapsedHours = seconds / (60 * 60);
        int s = seconds;
        if (elapsedHours > 0) {
            s = seconds - (elapsedHours * 60 * 60);
        }
        int elapsedMinutes = s / 60;
        int elapsedSeconds = s - elapsedHours * 60 * 60 - elapsedMinutes * 60;

        return new Time.TimeBuilder().hours(elapsedHours).minutes(elapsedMinutes).seconds(elapsedSeconds).build();
    }

    private static String formatTime(Time time) {
        if (time.hours > 0) {
            return String.format(ELAPSED_TEMPLATE_WITH_HOURS, time.hours, time.minutes, time.seconds);
        } else {
            return String.format(ELAPSED_TEMPLATE, time.minutes, time.seconds);
        }
    }

    @Data
    @Builder
    private static class Time {
        private int hours;
        private int minutes;
        private int seconds;
    }

    @AllArgsConstructor
    @Getter
    public static class Times {
        private String elapsed;
        private String remaining;
    }
}
