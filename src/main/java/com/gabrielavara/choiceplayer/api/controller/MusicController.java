package com.gabrielavara.choiceplayer.api.controller;

import com.gabrielavara.choiceplayer.dto.Mp3;
import com.gabrielavara.choiceplayer.api.service.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

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

    @ResponseBody
    @GetMapping(value = "/api/currentlyPlaying/albumArt", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getCurrentlyPlayingAlbumArt() {
        Optional<byte[]> currentlyPlayingAlbumArt = musicService.getCurrentlyPlayingAlbumArt();
        return currentlyPlayingAlbumArt.orElse(null);
    }
}
