package com.gabrielavara.musicplayer.controllers;

import java.io.ByteArrayInputStream;

import com.gabrielavara.musicplayer.api.service.Mp3;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PlaylistItem extends ListCell<Mp3> {
    @Override
    protected void updateItem(Mp3 mp3, boolean empty) {
        super.updateItem(mp3, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(getItemText(mp3));
            // setGraphic(getAlbumArt(mp3));
        }
    }

    private String getItemText(Mp3 mp3) {
        return mp3.getArtist() + " - " + mp3.getTitle();
    }

    private ImageView getAlbumArt(Mp3File mp3) {
        if (mp3.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3.getId3v2Tag();
            byte[] imageData = id3v2Tag.getAlbumImage();
            if (imageData != null) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
                Image image = new Image(inputStream);
                return new ImageView(image);
            }
        }
        return null;
    }
}
