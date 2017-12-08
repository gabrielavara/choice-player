package com.gabrielavara.choiceplayer.api.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gabrielavara.choiceplayer.controllers.PlayerController;
import com.gabrielavara.choiceplayer.views.TableItem;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import lombok.Setter;

@Service
public class MusicService {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.api.service.MusicService");

    @Autowired
    @Setter
    private PlayerController playerController;

    public List<Mp3> getPlayList() {
        log.info("getPlaylist called");
        return playerController.getMp3Files().stream().map(TableItem::getMp3).collect(Collectors.toList());
    }

    public Optional<Mp3> getCurrentlyPlaying() {
        log.info("getCurrentlyPlaying called");
        return playerController.getPlaylistChanger().getCurrentlyPlaying();
    }

    public Optional<byte[]> getCurrentlyPlayingAlbumArt() {
        log.info("getCurrentlyPlayingAlbumArt called");
        Optional<Mp3> currentlyPlaying = playerController.getPlaylistChanger().getCurrentlyPlaying();
        if (currentlyPlaying.isPresent()) {
            Mp3 mp3 = currentlyPlaying.get();
            Path path = Paths.get(mp3.getFilename());
            return getAlbumArtBytes(path);
        }
        return Optional.empty();
    }

    private Optional<byte[]> getAlbumArtBytes(Path path) {
        try {
            Mp3File mp3File = new Mp3File(path);
            if (mp3File.hasId3v2Tag()) {
                ID3v2 id3v2Tag = mp3File.getId3v2Tag();
                return Optional.ofNullable(id3v2Tag.getAlbumImage());
            }
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            log.error("Could not load mp3: {}", e.getMessage());
        }
        return Optional.empty();
    }
}
