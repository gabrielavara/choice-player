package com.gabrielavara.musicplayer.controllers;

import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

import com.gabrielavara.musicplayer.model.PlaylistLoader;
import com.jfoenix.controls.JFXListView;
import com.mpatric.mp3agic.Mp3File;

import de.felixroske.jfxsupport.FXMLController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

@FXMLController
public class PlayerController implements Initializable {
    @FXML
    private JFXListView<Mp3File> playlist;

    private PlaylistLoader playlistLoader = new PlaylistLoader();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Mp3File> files = playlistLoader.load(Paths.get("src/test/resources/mp3folder"));
        ObservableList<Mp3File> mp3Files = FXCollections.observableArrayList(files);
        playlist.setItems(mp3Files);
        playlist.setCellFactory(listView -> new PlaylistItem());
    }
}
