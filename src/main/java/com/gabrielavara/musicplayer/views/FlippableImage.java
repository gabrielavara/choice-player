package com.gabrielavara.musicplayer.views;

import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class FlippableImage extends StackPane {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.musicplayer.views.FlippableImage");

    private Timeline flipForward;
    private Timeline flipBackward;
    private boolean isFlipped = false;
    private static final double FLIP_SECS = 0.3;
    private ImageView back;
    private ImageView front;

    public FlippableImage() {
        this.setRotationAxis(Rotate.Y_AXIS);
        back = new ImageView();
        back.setEffect(new DropShadow());
        back.setFitHeight(250);
        back.setFitWidth(250);
        front = new ImageView();
        front.setEffect(new DropShadow());
        front.setFitHeight(250);
        front.setFitWidth(250);

        loadDefaultAlbumArt();

        Rotate backRot = new Rotate(180, Rotate.Y_AXIS);
        backRot.setPivotX(back.prefWidth(USE_PREF_SIZE) / 2);
        back.getTransforms().add(backRot);
        this.getChildren().addAll(back, front);

        flipForward = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(this.rotateProperty(), 0d)),
                        new KeyFrame(Duration.seconds(FLIP_SECS / 2), t -> back.toFront(),
                                        new KeyValue(this.rotateProperty(), 90d)),
                        new KeyFrame(Duration.seconds(FLIP_SECS), new KeyValue(this.rotateProperty(), 180d)));

        flipBackward = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(this.rotateProperty(), 180d)),
                        new KeyFrame(Duration.seconds(FLIP_SECS / 2), t -> front.toFront(),
                                        new KeyValue(this.rotateProperty(), 90d)),
                        new KeyFrame(Duration.seconds(FLIP_SECS), new KeyValue(this.rotateProperty(), 0d)));
    }

    private void loadDefaultAlbumArt() {
        Image image = getDefaultImage();
        front.setImage(image);
    }

    public Image getDefaultImage() {
        try {
            FileInputStream inputStream = new FileInputStream("src/main/resources/images/defaultAlbumArt.jpg");
            Image image = new Image(inputStream);
            inputStream.close();
            return image;
        } catch (IOException e) {
            log.warn("Could not load default album art");
        }
        return null;
    }

    public void setImage(Image image) {
        if (isFlipped) {
            front.setImage(image);
        } else {
            back.setImage(image);
        }
        flip();
    }

    private void flip() {
        if (isFlipped) {
            flipBackward.play();
        } else {
            flipForward.play();
        }
        isFlipped = !isFlipped;
    }
}
