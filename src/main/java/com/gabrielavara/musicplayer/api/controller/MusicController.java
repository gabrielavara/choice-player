package com.gabrielavara.musicplayer.api.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gabrielavara.musicplayer.api.service.Mp3;
import com.gabrielavara.musicplayer.api.service.MusicService;

@RestController
public class MusicController {
    @Autowired
    private MusicService musicService;

    @GetMapping("/api/playlist")
    public List<Mp3> getPlaylist() {
        return musicService.getPlayList();
    }

    @GetMapping("/api/currentlyPlaying")
    public Optional<Mp3> getCurrentlyPlaying() {
        return musicService.getCurrentlyPlaying();
    }
}
