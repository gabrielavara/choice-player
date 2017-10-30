package com.gabrielavara.musicplayer.api.service;

import java.nio.file.Paths;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gabrielavara.musicplayer.model.PlaylistLoader;
import com.mpatric.mp3agic.Mp3File;

@Service
public class MusicService {
    public List<Mp3File> getPlayList() {
        return new PlaylistLoader().load(Paths.get("src/test/resources/mp3folder"));
    }
}
