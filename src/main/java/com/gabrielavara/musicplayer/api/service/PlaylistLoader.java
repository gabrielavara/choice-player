package com.gabrielavara.musicplayer.api.service;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PlaylistLoader {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.musicplayer.api.service.PlaylistLoader");

    public List<Mp3> load(Path folder) {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(folder)) {
            Stream<Path> paths = StreamSupport.stream(directoryStream.spliterator(), false);
            return paths.filter(isMp3()).sorted(new CreationTimeComparator()).map(PlaylistLoader::createMp3File)
                    .filter(Objects::nonNull).collect(Collectors.toList());
        } catch (IOException | DirectoryIteratorException e) {
            log.error("Could not find folder: {0}. Message: {1}", folder, e.getMessage());
        }
        return Collections.emptyList();
    }

    private Predicate<Path> isMp3() {
        return path -> path.getFileName().toString().endsWith(".mp3");
    }

    private static Mp3 createMp3File(Path path) {
        try {
            Mp3File mp3File = new Mp3File(path);
            return new Mp3(mp3File);
        } catch (IOException e) {
            log.error("Could not load file: {0}. Message: {1}", path, e.getMessage());
        } catch (UnsupportedTagException e) {
            log.error("Unsupported tag in file: {0}. Message: {1}", path, e.getMessage());
        } catch (InvalidDataException e) {
            log.error("Invalid data for file: {0}. Message: {1}", path, e.getMessage());
        }
        return null;
    }
}
