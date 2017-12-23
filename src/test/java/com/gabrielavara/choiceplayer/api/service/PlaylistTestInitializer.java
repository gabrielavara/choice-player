package com.gabrielavara.choiceplayer.api.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.junit.Before;

public class PlaylistTestInitializer {
    protected static final String TEST_RESOURCES = "src/test/resources/mp3";
    private static final Path NEWER_FILE_PATH = Paths.get("src/test/resources/mp3/testNewer.mp3");
    private static final Path OLDER_FILE_PATH = Paths.get("src/test/resources/mp3/testOlder.mp3");
    private static final Path ALBUM_1_PATH = Paths.get("src/test/resources/mp3/testAlbum1.mp3");
    private static final Path ALBUM_2_PATH = Paths.get("src/test/resources/mp3/testAlbum2.mp3");

    @Before
    public void setup() throws IOException {
        Date now = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        Date twoHoursAgo = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).minusHours(2).toInstant());
        Date oneHourAgo = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).minusHours(1).toInstant());
        Date threeHoursAgo = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).minusHours(3).toInstant());
        setFileCreationDate(NEWER_FILE_PATH, now);
        setFileCreationDate(ALBUM_1_PATH, twoHoursAgo);
        setFileCreationDate(ALBUM_2_PATH, oneHourAgo);
        setFileCreationDate(OLDER_FILE_PATH, threeHoursAgo);
    }

    private void setFileCreationDate(Path filePath, Date creationDate) throws IOException {
        BasicFileAttributeView attributes = Files.getFileAttributeView(filePath, BasicFileAttributeView.class);
        FileTime time = FileTime.fromMillis(creationDate.getTime());
        attributes.setTimes(time, time, time);
    }
}
