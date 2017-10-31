package com.gabrielavara.musicplayer.controllers;

import com.gabrielavara.musicplayer.api.service.Mp3;
import com.gabrielavara.musicplayer.api.service.MusicService;
import com.gabrielavara.musicplayer.views.AnimatingLabel;
import com.gabrielavara.musicplayer.views.FlippableImage;
import com.jfoenix.controls.JFXListView;
import de.felixroske.jfxsupport.FXMLController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
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
    private StackPane albumArtStackPane;
    @FXML
    private JFXListView<Mp3> playlist;
    @FXML
    private VBox currentlyPlayingBox;

    @Autowired
    private MusicService musicService;

    @Setter
    private MediaPlayer mediaPlayer;

    private FlippableImage flippableAlbumArt = new FlippableImage();
    private AnimatingLabel artist;
    private AnimatingLabel title;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        flippableAlbumArt = new FlippableImage();
        artist = new AnimatingLabel("Artist", 20);
        title = new AnimatingLabel("Title", 16);

        playlist.getSelectionModel().selectedItemProperty().addListener(new PlaylistSelectionChangedListener(this));
        loadPlaylist();
        albumArtStackPane.getChildren().add(flippableAlbumArt);
        VBox.setMargin(artist, new Insets(6, 24, 6, 24));
        VBox.setMargin(title, new Insets(6, 24, 6, 24));

        currentlyPlayingBox.getChildren().add(1, artist);
        currentlyPlayingBox.getChildren().add(2, title);
    }

    private void loadPlaylist() {
        List<Mp3> files = musicService.getPlayList();
        ObservableList<Mp3> mp3Files = FXCollections.observableArrayList(files);
        playlist.setItems(mp3Files);
        playlist.setCellFactory(listView -> new PlaylistItem());
    }

    public Optional<Mp3> getCurrentlyPlaying() {
        List<Mp3> playing = playlist.getItems().stream().filter(Mp3::isCurrentlyPlaying).collect(Collectors.toList());
        return playing.size() == 1 ? Optional.of(playing.get(0)) : Optional.empty();
    }

    void setAlbumArt() {
        Optional<byte[]> albumArtData = musicService.getCurrentlyPlayingAlbumArt();
        if (albumArtData.isPresent()) {
            setExistingAlbumArt(albumArtData.get());
        } else {
            flippableAlbumArt.setImage(flippableAlbumArt.getDefaultImage());
        }
    }

    private void setExistingAlbumArt(byte[] albumArtData) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(albumArtData);
        Image image = new Image(inputStream);
        flippableAlbumArt.setImage(image);
    }
}
