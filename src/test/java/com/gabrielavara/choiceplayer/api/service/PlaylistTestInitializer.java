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
}
