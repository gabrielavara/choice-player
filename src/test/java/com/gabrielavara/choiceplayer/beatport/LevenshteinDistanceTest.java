package com.gabrielavara.choiceplayer.beatport;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LevenshteinDistanceTest {

    @Test
    public void testOneDistance() {
        testDistanceOf("a", "b", 1);
    }

    @Test
    public void testTwoDistance() {
        testDistanceOf("abc", "ade", 2);
    }

    private void testDistanceOf(String a, String b, int expected) {
        //when
        int distance = LevenshteinDistance.calculate(a, b);

        //then
        assertEquals(expected, distance);
    }
}