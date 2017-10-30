package com.gabrielavara.musicplayer.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreationTimeComparator implements Comparator<Path> {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.musicplayer.model.CreationTimeComparator");

    @Override
    public int compare(Path path1, Path path2) {
        try {
            FileTime creationTime1 = getCreationTime(path1);
            FileTime creationTime2 = getCreationTime(path2);
            return creationTime1.compareTo(creationTime2);
        } catch (IOException e) {
            log.error("Could not get creation time: {0}", e.getMessage());
        }
        return -1;
    }

    private FileTime getCreationTime(Path path1) throws IOException {
        return Files.getFileAttributeView(path1, BasicFileAttributeView.class).readAttributes().creationTime();
    }
}
