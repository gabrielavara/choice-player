package com.gabrielavara.choiceplayer.util;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class TimeSliderConverterTest {
    private static final double EPSILON = 1e-15;
    private TimeSliderConverter converter = new TimeSliderConverter();

    @Before
    public void setup() {
        converter.setLength(60 * 1000);
    }

    @Test
    public void testToString() {
        // when
        String label = converter.toString(50d);

        // test
        assertEquals("0:30", label);
    }

    @SuppressWarnings("squid:S3415")
    @Test
    public void fromString() {
        // when
        Double value = converter.fromString("0:30");

        // test
        assertEquals(50, value, EPSILON);
    }

}