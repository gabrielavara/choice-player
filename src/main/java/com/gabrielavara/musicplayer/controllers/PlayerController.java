package com.gabrielavara.musicplayer.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;

import com.gabrielavara.musicplayer.api.service.MusicService;
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

    @Autowired
    private MusicService musicService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Mp3File> files = musicService.getPlayList();
        ObservableList<Mp3File> mp3Files = FXCollections.observableArrayList(files);
        playlist.setItems(mp3Files);
        playlist.setCellFactory(listView -> new PlaylistItem());
    }
}
