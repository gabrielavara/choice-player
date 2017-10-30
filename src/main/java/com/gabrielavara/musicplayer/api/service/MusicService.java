package com.gabrielavara.musicplayer.api.service;

import java.nio.file.Paths;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class MusicService {
    public List<Mp3> getPlayList() {
        return new PlaylistLoader().load(Paths.get("src/test/resources/mp3folder"));
    }
}
