package com.gabrielavara.choiceplayer.views;

import static com.gabrielavara.choiceplayer.Constants.DEFAULT_ALBUM_ART;
import static com.gabrielavara.choiceplayer.Constants.SPLASH_ANIMATION_DURATION;
import static de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon.THUMB_DOWN;
import static de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon.THUMB_UP;
import static javafx.animation.Animation.INDEFINITE;
import static javafx.geometry.Pos.CENTER;
import static javafx.scene.transform.Rotate.Y_AXIS;
import static javafx.util.Duration.ZERO;

import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.felixroske.jfxsupport.SplashScreen;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class ChoicePlayerSplashScreen extends SplashScreen {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.views.ChoicePlayerSplashScreen");

    private ImageView imageView;

    @Override
    public Parent getParent() {
        loadImage();

        MaterialDesignIconView like = new MaterialDesignIconView(THUMB_UP);
        like.setFill(new Color(1, 1, 1, 1));
        like.setSize("36");
        StackPane likeStackPane = new StackPane();
        likeStackPane.setAlignment(CENTER);
        likeStackPane.getChildren().add(like);

        MaterialDesignIconView disLike = new MaterialDesignIconView(THUMB_DOWN);
        disLike.setFill(new Color(1, 1, 1, 1));
        disLike.setSize("36");
        StackPane disLikeStackPane = new StackPane();
        disLikeStackPane.setAlignment(CENTER);
        disLikeStackPane.getChildren().add(disLike);

        HBox hBox = new HBox();
        hBox.getChildren().addAll(likeStackPane, imageView, disLikeStackPane);

        rotateImage();

        rotateIcon(like);
        rotateIcon(disLike);

        hBox.setBackground(new Background(new BackgroundFill(new Color(0.22, 0.22, 0.22, 1), null, null)));
        hBox.setMinHeight((36 + 75) * 2);

        return hBox;
    }

    private void rotateIcon(MaterialDesignIconView like) {
        Rotate rotation = new Rotate();
        rotation.pivotXProperty().bind(new SimpleDoubleProperty(36 + 75));
        rotation.pivotYProperty().bind(new SimpleDoubleProperty(36 + 75));

        like.getTransforms().add(rotation);

        Timeline timeline = new Timeline(new KeyFrame(ZERO, new KeyValue(rotation.angleProperty(), 0)),
                        new KeyFrame(Duration.seconds(SPLASH_ANIMATION_DURATION), new KeyValue(rotation.angleProperty(), 360)));
        timeline.setCycleCount(INDEFINITE);
        timeline.play();
    }

    private void rotateImage() {
        RotateTransition rotateTransition = new RotateTransition(Duration.millis(SPLASH_ANIMATION_DURATION), imageView);
        rotateTransition.setAxis(Y_AXIS);
        rotateTransition.setToAngle(720);
        rotateTransition.setCycleCount(INDEFINITE);
        rotateTransition.setAutoReverse(true);
        rotateTransition.play();
    }

    private void loadImage() {
        try (FileInputStream inputStream = new FileInputStream(DEFAULT_ALBUM_ART)) {
            Image image = new Image(inputStream, 150, 150, true, false);
            imageView = new ImageView(image);
        } catch (IOException e) {
            log.error("Could not load default image");
        }
    }

    @Override
    public boolean visible() {
        return false;
    }
}
