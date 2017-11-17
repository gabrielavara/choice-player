package com.gabrielavara.musicplayer.views;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gabrielavara.musicplayer.api.service.Mp3;
import com.gabrielavara.musicplayer.util.TimeFormatter;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import lombok.Getter;

public class PlaylistItemController {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.musicplayer.views.PlaylistItemController");
    @Getter
    @FXML
    private HBox rootNode;
    @FXML
    private Label indexLabel;
    @FXML
    private Label artistLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private Label lengthLabel;

    public PlaylistItemController() {
        loadFxml();
    }

    private void loadFxml() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/playlist_item.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            log.error("Could not load playlist_item.fxml", e);
        }
    }

    public void setLabels(int index, Mp3 mp3) {
        indexLabel.setText(Integer.toString(index));
        artistLabel.setText(mp3.getArtist());
        titleLabel.setText(mp3.getTitle());
        int seconds = (int) mp3.getLength() / 1000;
        lengthLabel.setText(TimeFormatter.getFormattedLength(seconds));
    }
}
