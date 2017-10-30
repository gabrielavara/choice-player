package com.gabrielavara.musicplayer.api.service;

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
    private final Path path = Paths.get("src/test/resources/mp3folder");
    private final Path newerPath = Paths.get("src/test/resources/mp3folder/testNewer.mp3");

    @Before
    public void setup() throws IOException {
        Date now = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        setFileCreationDate(newerPath, now);
    }

    private void setFileCreationDate(Path filePath, Date creationDate) throws IOException{
        BasicFileAttributeView attributes = Files.getFileAttributeView(filePath, BasicFileAttributeView.class);
        FileTime time = FileTime.fromMillis(creationDate.getTime());
        attributes.setTimes(time, time, time);
    }

    @Test
    public void testLoad() throws IOException {
        // given
        PlaylistLoader playlistLoader = new PlaylistLoader();

        // when
        List<Mp3> mp3Files = playlistLoader.load(path);

        // then
        assertEquals(mp3Files.size(), 2);
        assertEquals(mp3Files.get(0).getArtist(), "Me");
        assertEquals(mp3Files.get(0).getTitle(), "Test Older");
        assertEquals(mp3Files.get(1).getArtist(), "Me");
        assertEquals(mp3Files.get(1).getTitle(), "Test Newer");
    }
}