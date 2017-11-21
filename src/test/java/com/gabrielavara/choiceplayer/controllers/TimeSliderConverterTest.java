package com.gabrielavara.choiceplayer.controllers;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TimeSliderConverterTest {
    private static final double EPSILON = 1e-15;
    private TimeSliderConverter converter = new TimeSliderConverter();

    @Before
    public void setup() {
        converter.setLength(60 * 1000);
    }

    @Test
    public void testToString() throws Exception {
        // when
        String label = converter.toString(50d);

        // test
        assertEquals("0:30", label);
    }

    @Test
    public void fromString() throws Exception {
        // when
        Double value = converter.fromString("0:30");

        // test
        assertEquals(50, value, EPSILON);
    }

}