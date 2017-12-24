package com.gabrielavara.choiceplayer.controls.animatedalbumart;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnimatedAlbumArt extends AnchorPane {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.controls.animatedalbumart.AnimatedAlbumArtController");

    private AnimatedAlbumArtController controller;

    public AnimatedAlbumArt() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/controls/animated_album_art.fxml"));

        fxmlLoader.setControllerFactory(param -> {
            controller = new AnimatedAlbumArtController();
            return controller;
        });

        try {
            Node view = (Node) fxmlLoader.load();
            getChildren().add(view);
        } catch (IOException e) {
            log.error("Could not load animated button");
            throw new IllegalStateException("Could not load animated button", e);
        }
    }

    public void setImage(Image albumArt) {
        controller.setImage(albumArt);
    }

    public void hover(boolean hover) {
        controller.hover(hover);
    }
}
