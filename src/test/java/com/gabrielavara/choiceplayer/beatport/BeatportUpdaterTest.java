package com.gabrielavara.choiceplayer.beatport;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BeatportUpdaterTest {
    @Test
    public void testGetTitle() {
        // given
        BeatportTrack track = new BeatportTrack("", singletonList("artist"), "hello feat. artist", "mix", singletonList("genre"), 120, 360);

        // when
        String title = BeatportUpdater.getTitle(track);

        // then
        assertEquals("hello", title);
    }

    @Test
    public void testGetTitleArtistFirst() {
        // given
        BeatportTrack track = new BeatportTrack("", singletonList("artist"), "artist hello", "mix", singletonList("genre"), 120, 360);

        // when
        String title = BeatportUpdater.getTitle(track);

        // then
        assertEquals("artist hello", title);
    }

    @Test
    public void testGetTitleWithoutArtist() {
        // given
        BeatportTrack track = new BeatportTrack("", singletonList("artist"), "hello", "mix", singletonList("genre"), 120, 360);

        // when
        String title = BeatportUpdater.getTitle(track);

        // then
        assertEquals("hello", title);
    }
}