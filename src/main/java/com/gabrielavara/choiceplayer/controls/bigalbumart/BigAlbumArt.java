package com.gabrielavara.choiceplayer.controls.bigalbumart;

import com.gabrielavara.choiceplayer.controls.AnimationDirection;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class BigAlbumArt extends StackPane {
    private static final String COULD_NOT_LOAD = "Could not load big album art";
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.controls.albumart.BigAlbumArt");

    private BigAlbumArtController controller;

    public BigAlbumArt() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/controls/big_album_art.fxml"));

        fxmlLoader.setControllerFactory(param -> {
            controller = new BigAlbumArtController();
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

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public void setImage(Optional<byte[]> albumArtData, Direction direction, Runnable afterFinished) {
        controller.setImage(albumArtData, direction, afterFinished);
    }

    public void animatePlayPause(AnimationDirection animationDirection) {
        controller.animatePlayPause(animationDirection);
    }
}
