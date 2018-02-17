package com.gabrielavara.choiceplayer.playlist;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.readAllLines;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabrielavara.choiceplayer.views.PlaylistItemView;

public class PlaylistCache {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.playlist.PlaylistCache");
    private static final String PLAYLIST_CACHE_JSON = "playlistCache.json";

    private PlaylistCache() {
    }

    public static List<PlaylistItemView> load() {
        log.info("Load playlist cache file");
        Path path = Paths.get(PLAYLIST_CACHE_JSON);
        if (path.toFile().exists()) {
            return loadFile(path);
        } else {
            log.info("Could not find playlist cache file");
            return new ArrayList<>();
        }
    }

    private static List<PlaylistItemView> loadFile(Path path) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String content = readAllLines(path, UTF_8).stream().collect(Collectors.joining(" "));
            return objectMapper.readValue(content, new TypeReference<List<PlaylistItemView>>() {
            });
        } catch (IOException e) {
            log.error("Could not load playlist cache", e);
            return new ArrayList<>();
        }
    }

    public static void save(List<PlaylistItemView> playlist) {
        log.info("Save playlist cache file");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String content = objectMapper.writeValueAsString(playlist);
            Path path = Paths.get(PLAYLIST_CACHE_JSON);
            if (content != null) {
                Files.write(path, content.getBytes(), WRITE, CREATE, TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            log.error("Could not save playlist cache");
        }
    }
}
