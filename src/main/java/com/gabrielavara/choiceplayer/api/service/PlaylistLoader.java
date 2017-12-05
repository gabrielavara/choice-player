package com.gabrielavara.choiceplayer.api.service;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

public class PlaylistLoader {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.api.service.PlaylistLoader");

    public List<Mp3> load(Path folder) {
        log.info("Start loading playlist");
        if (!folder.toFile().exists()) {
            folder = Paths.get("src/test/resources/mp3folder");
        }
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(folder)) {
            Map<String, List<Mp3>> albums = getAlbums(directoryStream);
            TreeMap<Double, List<Mp3>> sortedAlbums = getSortedAlbums(albums);
            List<Mp3> sortedPlaylist = getSortedPlaylist(sortedAlbums);
            log.info("Playlist loaded");
            return sortedPlaylist;
        } catch (IOException | DirectoryIteratorException e) {
            log.error("Could not find folder: {}. Message: {}", folder, e.getMessage());
        }
        return Collections.emptyList();
    }

    private Map<String, List<Mp3>> getAlbums(DirectoryStream<Path> directoryStream) {
        Stream<Path> paths = StreamSupport.stream(directoryStream.spliterator(), false);
        return paths.filter(isMp3()).map(PlaylistLoader::createMp3File).filter(Objects::nonNull).collect(Collectors.groupingBy(Mp3::getAlbum));
    }

    private TreeMap<Double, List<Mp3>> getSortedAlbums(Map<String, List<Mp3>> albums) {
        TreeMap<Double, List<Mp3>> sortedAlbums = new TreeMap<>();
        for (List<Mp3> tracks : albums.values()) {
            Double averageTime = tracks.stream().map(PlaylistLoader::getCreationTime)
                    .collect(Collectors.averagingLong(FileTime::toMillis));
            sortedAlbums.put(averageTime, tracks);
        }
        return sortedAlbums;
    }

    private List<Mp3> getSortedPlaylist(TreeMap<Double, List<Mp3>> sortedAlbums) {
        List<Mp3> sortedPlayList = new ArrayList<>();
        sortedAlbums.forEach((averageTime, tracks) -> {
            tracks.sort(Comparator.comparing(Mp3::getTrack));
            sortedPlayList.addAll(tracks);
        });
        return sortedPlayList;
    }

    private Predicate<Path> isMp3() {
        return path -> path.getFileName().toString().endsWith(".mp3");
    }

    private static Mp3 createMp3File(Path path) {
        try {
            Mp3File mp3File = new Mp3File(path);
            return new Mp3(mp3File);
        } catch (IOException e) {
            log.error("Could not load file: {}. Message: {}", path, e.getMessage());
        } catch (UnsupportedTagException e) {
            log.error("Unsupported tag in file: {}. Message: {}", path, e.getMessage());
        } catch (InvalidDataException e) {
            log.error("Invalid data for file: {}. Message: {}", path, e.getMessage());
        }
        return null;
    }

    private static FileTime getCreationTime(Mp3 mp3) {
        try {
            Path path = Paths.get(mp3.getFilename());
            return Files.getFileAttributeView(path, BasicFileAttributeView.class).readAttributes().creationTime();
        } catch (IOException e) {
            log.error("Could not get creation time: {}", e.getMessage());
            return FileTime.fromMillis(System.currentTimeMillis());
        }
    }
}
