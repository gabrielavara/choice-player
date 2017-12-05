package com.gabrielavara.choiceplayer.api.service;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class PlaylistLoaderTest {
    private final Path olderFilePath = Paths.get("src/test/resources/mp3folder");
    private final Path newerFilePath = Paths.get("src/test/resources/mp3folder/testNewer.mp3");
    private final Path album1Path = Paths.get("src/test/resources/mp3folder/testAlbum1.mp3");
    private final Path album2Path = Paths.get("src/test/resources/mp3folder/testAlbum2.mp3");

    @Before
    public void setup() throws IOException {
        Date now = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        Date twoMinutesAgo = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).minusMinutes(2).toInstant());
        Date oneMinuteAgo = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).minusMinutes(1).toInstant());
        setFileCreationDate(newerFilePath, now);
        setFileCreationDate(album1Path, twoMinutesAgo);
        setFileCreationDate(album2Path, oneMinuteAgo);
    }

    private void setFileCreationDate(Path filePath, Date creationDate) throws IOException {
        BasicFileAttributeView attributes = Files.getFileAttributeView(filePath, BasicFileAttributeView.class);
        FileTime time = FileTime.fromMillis(creationDate.getTime());
        attributes.setTimes(time, time, time);
    }

    @Test
    public void testLoad() throws IOException {
        // given
        PlaylistLoader playlistLoader = new PlaylistLoader();

        // when
        List<Mp3> mp3Files = playlistLoader.load(olderFilePath);

        // then
        assertEquals(4, mp3Files.size());
        assertEquals("Test Artist", mp3Files.get(0).getArtist());
        assertEquals("Test Older Title", mp3Files.get(0).getTitle());
        assertEquals("1", mp3Files.get(0).getTrack());

        assertEquals("Artist 2", mp3Files.get(1).getArtist());
        assertEquals("Track 1", mp3Files.get(1).getTitle());
        assertEquals("1", mp3Files.get(1).getTrack());

        assertEquals("Artist 1", mp3Files.get(2).getArtist());
        assertEquals("Track 2", mp3Files.get(2).getTitle());
        assertEquals("2", mp3Files.get(2).getTrack());

        assertEquals("The Super Artist Test", mp3Files.get(3).getArtist());
        assertEquals("Test Newer Title", mp3Files.get(3).getTitle());
        assertEquals("1", mp3Files.get(3).getTrack());
    }
}