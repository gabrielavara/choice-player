package com.gabrielavara.choiceplayer.api.service;

import static java.nio.file.Files.readAllLines;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Cache {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.api.service.Cache");

    private static final String PLAYLIST_CACHE_JSON = "playlistCache.json";
    private Map<String, Mp3> cacheMap = new HashMap<>();

    public void load() {
        cacheMap.clear();
        Path path = Paths.get(PLAYLIST_CACHE_JSON);
        if (path.toFile().exists()) {
            loadFile(path);
        } else {
            log.info("Could not find playlist cache file");
        }
    }

    private void loadFile(Path path) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String content = readAllLines(path).stream().collect(Collectors.joining(" "));
            cacheMap = objectMapper.readValue(content, new TypeReference<Map<String, Mp3>>() {
            });
        } catch (IOException e) {
            log.info("Could not load playlist cache", e);
        }
    }

    public Mp3 get(String key) {
        return cacheMap.get(key);
    }

    public void put(String key, Mp3 mp3) {
        cacheMap.put(key, mp3);
    }

    public void save() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String content = objectMapper.writeValueAsString(cacheMap);
            Path path = Paths.get(PLAYLIST_CACHE_JSON);
            Files.write(path, content.getBytes());
        } catch (IOException e) {
            log.error("Could not save playlist cache");
        }
    }
}
