package com.gabrielavara.choiceplayer.controllers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TimeSliderConverterTest {
    private static final double EPSILON = 1e-15;
    private TimeSliderConverter converter = new TimeSliderConverter();

    @Test
    public void testToString() throws Exception {
        // given
        converter.setLength(60 * 1000);

        // when
        String label = converter.toString(0.5);

        // test
        assertEquals("0:30", label);
    }

    @Test
    public void fromString() throws Exception {
        // given
        converter.setLength(60 * 1000);

        // when
        Double value = converter.fromString("0:30");

        // test
        assertEquals(0.5, value, EPSILON);
    }

}