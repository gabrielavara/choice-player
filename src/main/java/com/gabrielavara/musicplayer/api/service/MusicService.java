package com.gabrielavara.musicplayer.api.service;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gabrielavara.musicplayer.controllers.PlayerController;

@Service
public class MusicService {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.musicplayer.api.service.MusicService");

    @Autowired
    private PlayerController playerController;

    public List<Mp3> getPlayList() {
        log.info("getPlaylist called");
        return new PlaylistLoader().load(Paths.get("src/test/resources/mp3folder"));
    }

    public Optional<Mp3> getCurrentlyPlaying() {
        log.info("getCurrentlyPlaying called");
        return playerController.getCurrentlyPlaying();
    }
}
