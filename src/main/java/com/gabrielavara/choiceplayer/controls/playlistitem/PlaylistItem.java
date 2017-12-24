package com.gabrielavara.choiceplayer.controls.playlistitem;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}
