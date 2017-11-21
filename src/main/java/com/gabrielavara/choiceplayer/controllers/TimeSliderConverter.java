package com.gabrielavara.choiceplayer.controllers;

import com.gabrielavara.choiceplayer.util.TimeFormatter;

import javafx.util.StringConverter;

public class TimeSliderConverter extends StringConverter<Double> {
    private long length;

    @Override
    public String toString(Double value) {
        int seconds = (int) (length * value / 1000);
        return TimeFormatter.getFormattedLength(seconds);
    }

    @Override
    public Double fromString(String label) {
        String[] parts = label.split(":");
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        if (parts.length == 3) {
            hours = Integer.valueOf(parts[0]);
            minutes = Integer.valueOf(parts[1]);
            seconds = Integer.valueOf(parts[2]);
        }
        if (parts.length == 2) {
            minutes = Integer.valueOf(parts[0]);
            seconds = Integer.valueOf(parts[1]);
        }
        int sumSeconds = hours * 60 * 60 + minutes * 60 + seconds;
        return (double) sumSeconds * 1000 / length;
    }

    void setLength(long length) {
        this.length = length;
    }
}
