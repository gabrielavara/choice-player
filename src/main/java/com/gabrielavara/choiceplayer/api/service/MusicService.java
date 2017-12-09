package com.gabrielavara.choiceplayer.api.service;

import com.gabrielavara.choiceplayer.controllers.PlayerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MusicService {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.api.service.MusicService");

    @Autowired
    private PlayerController playerController;

    public List<Mp3> getPlayList() {
        log.info("getPlaylist called");
        return playerController.getPlaylistUtil().getPlayList();
    }

    public Optional<Mp3> getCurrentlyPlaying() {
        log.info("getCurrentlyPlaying called");
        return playerController.getPlaylistUtil().getCurrentlyPlaying();
    }

    public Optional<byte[]> getCurrentlyPlayingAlbumArt() {
        log.info("getCurrentlyPlayingAlbumArt called");
        return playerController.getPlaylistUtil().getCurrentlyPlayingAlbumArt();
    }
}
