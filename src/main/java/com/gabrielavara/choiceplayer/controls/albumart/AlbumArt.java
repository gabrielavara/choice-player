package com.gabrielavara.choiceplayer.controls.albumart;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;

public class AlbumArt extends AnchorPane {
    private static final String COULD_NOT_LOAD = "Could not load album art";
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.controls.albumart.AlbumArt");

    private AlbumArtController controller;
    private boolean hoverAllowed = true;

    public AlbumArt() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/controls/album_art.fxml"));

        fxmlLoader.setControllerFactory(param -> {
            controller = new AlbumArtController();
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

    public void setHoverAllowed(boolean hoverAllowed) {
        this.hoverAllowed = hoverAllowed;
    }

    public void setImage(Image albumArt) {
        controller.setImage(albumArt);
    }

    public void hover(boolean hover) {
        if (hoverAllowed) {
            controller.hover(hover);
        }
    }

    public void setTheme() {
        controller.setTheme();
    }
}
