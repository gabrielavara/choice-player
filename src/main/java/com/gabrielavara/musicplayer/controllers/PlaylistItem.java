package com.gabrielavara.musicplayer.controllers;

import com.gabrielavara.musicplayer.api.service.Mp3;

import javafx.scene.control.ListCell;

public class PlaylistItem extends ListCell<Mp3> {
    @Override
    protected void updateItem(Mp3 mp3, boolean empty) {
        super.updateItem(mp3, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(getItemText(mp3));
        }
    }

    private String getItemText(Mp3 mp3) {
        return mp3.getArtist() + " - " + mp3.getTitle();
    }
}
