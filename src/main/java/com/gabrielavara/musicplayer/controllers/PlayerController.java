package com.gabrielavara.musicplayer.controllers;

import com.gabrielavara.musicplayer.api.service.Mp3;
import com.gabrielavara.musicplayer.api.service.MusicService;
import com.jfoenix.controls.JFXListView;
import de.felixroske.jfxsupport.FXMLController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaPlayer;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Getter
@FXMLController
public class PlayerController implements Initializable {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.musicplayer.controllers.PlayerController");

    @FXML
    private JFXListView<Mp3> playlist;
    @FXML
    private ImageView albumArt;
    @FXML
    private Label artist;
    @FXML
    private Label title;

    @Autowired
    private MusicService musicService;

    @Setter
    private MediaPlayer mediaPlayer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playlist.getSelectionModel().selectedItemProperty().addListener(new PlaylistSelectionChangedListener(this));

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
