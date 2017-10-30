package com.gabrielavara.musicplayer.controllers;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.gabrielavara.musicplayer.api.service.Mp3;
import com.gabrielavara.musicplayer.api.service.MusicService;
import com.jfoenix.controls.JFXListView;

import de.felixroske.jfxsupport.FXMLController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

@FXMLController
public class PlayerController implements Initializable {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.musicplayer.controllers.PlayerController");

    @FXML
    private JFXListView<Mp3> playlist;

    @Autowired
    private MusicService musicService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playlist.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Mp3>() {
            @Override
            public void changed(ObservableValue<? extends Mp3> observable, Mp3 oldValue, Mp3 newValue) {
                log.info("Playlist selection changed from: {}, to {}", oldValue, newValue);
            }
        });

        List<Mp3> files = musicService.getPlayList();
        ObservableList<Mp3> mp3Files = FXCollections.observableArrayList(files);
        playlist.setItems(mp3Files);
        playlist.setCellFactory(listView -> new PlaylistItem());
    }

    public Optional<Mp3> getCurrentlyPlaying() {
        List<Mp3> playing = playlist.getItems().stream().filter(Mp3::isCurrentlyPlaying).collect(Collectors.toList());
        if (playing.size() == 1) {
            return Optional.of(playing.get(0));
        }
        return Optional.empty();
    }
}
