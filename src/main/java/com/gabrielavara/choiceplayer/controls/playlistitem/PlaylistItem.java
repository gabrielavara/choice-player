package com.gabrielavara.choiceplayer.controls.playlistitem;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gabrielavara.choiceplayer.controls.albumart.AlbumArt;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

public class PlaylistItem extends HBox {
    private static final String COULD_NOT_LOAD = "Could not load playlist item";
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.controls.albumart.PlaylistItem");

    private PlaylistItemController controller;

    public PlaylistItem() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/controls/playlist_item.fxml"));

        fxmlLoader.setControllerFactory(param -> {
            controller = new PlaylistItemController();
            return controller;
        });

        try {
            Node view = fxmlLoader.load();
            getChildren().add(view);
        } catch (IOException e) {
            log.error(COULD_NOT_LOAD);
            throw new IllegalStateException(COULD_NOT_LOAD, e);
        }
    }

    public void setIndex(String index) {
        controller.setIndex(index);
    }

    public void setArtist(String artist) {
        controller.setArtist(artist);
    }

    public void setTitle(String title) {
        controller.setTitle(title);
    }

    public void setLength(String length) {
        controller.setLength(length);
    }

    public AlbumArt getAlbumArt() {
        return controller.getAlbumArt();
    }

    public void animateToState(PlaylistItemState state) {
        controller.animateToState(state);
    }

    public void setState(boolean currentlyPlaying) {
        controller.setState(currentlyPlaying);
    }

    public void changeTheme() {
        controller.changeTheme();
    }
}
