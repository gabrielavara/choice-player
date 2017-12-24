package com.gabrielavara.choiceplayer.controls.playlistitem;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlaylistItem extends HBox {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.controls.animatedalbumart.AnimatedAlbumArtController");

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
            log.error("Could not load animated button");
            throw new IllegalStateException("Could not load animated button", e);
        }
    }
}
