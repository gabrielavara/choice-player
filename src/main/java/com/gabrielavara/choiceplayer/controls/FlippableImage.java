package com.gabrielavara.choiceplayer.controls;

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
    private static final int SIZE = 350;
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.controls.FlippableImage");

    private Timeline flipForward;
    private Timeline flipBackward;
    private boolean isFlipped = false;
    private static final double FLIP_SECS = 0.6;
    private ImageView back;
    private ImageView front;

    public FlippableImage() {
        setRotationAxis(Rotate.Y_AXIS);
        back = new ImageView();
        back.setEffect(new DropShadow());
        back.setFitHeight(SIZE);
        back.setFitWidth(SIZE);
        front = new ImageView();
        front.setEffect(new DropShadow());
        front.setFitHeight(SIZE);
        front.setFitWidth(SIZE);

        loadDefaultAlbumArt();

        Rotate backRot = new Rotate(180, Rotate.Y_AXIS);
        backRot.setPivotX(back.prefWidth(USE_PREF_SIZE) / 2);
        back.getTransforms().add(backRot);
        getChildren().addAll(back, front);

        flipForward = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(scaleXProperty(), 1d), new KeyValue(scaleYProperty(), 1d), new KeyValue(rotateProperty(), 0d)),
                new KeyFrame(Duration.seconds(FLIP_SECS / 4 * 1), new KeyValue(scaleXProperty(), 0.8d), new KeyValue(scaleYProperty(), 0.8d)),
                new KeyFrame(Duration.seconds(FLIP_SECS / 4 * 2), t -> back.toFront(), new KeyValue(rotateProperty(), 90d)),
                new KeyFrame(Duration.seconds(FLIP_SECS / 4 * 3), new KeyValue(rotateProperty(), 180d)),
                new KeyFrame(Duration.seconds(FLIP_SECS), new KeyValue(scaleXProperty(), 1d), new KeyValue(scaleYProperty(), 1d)));

        flipBackward = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(scaleXProperty(), 1d), new KeyValue(scaleYProperty(), 1d), new KeyValue(rotateProperty(), 180d)),
                new KeyFrame(Duration.seconds(FLIP_SECS / 4 * 1), new KeyValue(scaleXProperty(), 0.8d), new KeyValue(scaleYProperty(), 0.8d)),
                new KeyFrame(Duration.seconds(FLIP_SECS / 4 * 2), t -> front.toFront(), new KeyValue(rotateProperty(), 90d)),
                new KeyFrame(Duration.seconds(FLIP_SECS / 4 * 3), new KeyValue(rotateProperty(), 0d)),
                new KeyFrame(Duration.seconds(FLIP_SECS), new KeyValue(scaleXProperty(), 1d), new KeyValue(scaleYProperty(), 1d)));
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
