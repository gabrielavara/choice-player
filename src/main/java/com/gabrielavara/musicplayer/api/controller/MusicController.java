package com.gabrielavara.musicplayer.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gabrielavara.musicplayer.api.service.MusicService;
import com.mpatric.mp3agic.Mp3File;

@RestController
public class MusicController {
    @Autowired
    private MusicService musicService;

    @GetMapping("/api/playlist")
    public List<Mp3File> getPlaylist() {
        return musicService.getPlayList();
    }
}
