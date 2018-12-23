package com.gabrielavara.choiceplayer.views;

import static com.gabrielavara.choiceplayer.Constants.DEFAULT_ALBUM_ART_PNG;
import static com.gabrielavara.choiceplayer.Constants.LONG_ANIMATION_DURATION;

import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfoenix.controls.JFXSpinner;

import de.felixroske.jfxsupport.SplashScreen;
import javafx.animation.FadeTransition;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class ChoicePlayerSplashScreen extends SplashScreen {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.views.ChoicePlayerSplashScreen");

    private ImageView imageView;

    @Override
    public Parent getParent() {
        loadImage();

        Circle circle = new Circle(350 / 2, new Color(0, 0, 0, 0.4));
        circle.setStrokeWidth(0);

        JFXSpinner spinner = new JFXSpinner();
        spinner.setTranslateX(-1);
        spinner.setTranslateY(-1);

        StackPane imageStackPane = new StackPane();
        imageStackPane.getChildren().addAll(circle, spinner, imageView);
        imageStackPane.setBackground(Background.EMPTY);

        FadeTransition fadeTransition = getFadeTransition(imageStackPane);
        fadeTransition.play();

        imageStackPane.setOpacity(0);
        return imageStackPane;
    }

    private void loadImage() {
        try (FileInputStream inputStream = new FileInputStream(DEFAULT_ALBUM_ART_PNG)) {
            Image image = new Image(inputStream, 350, 350, true, true);
            imageView = new ImageView(image);
        } catch (IOException e) {
            log.error("Could not load default image");
        }
    }

    private FadeTransition getFadeTransition(Pane pane) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(LONG_ANIMATION_DURATION), pane);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.setDelay(Duration.millis(LONG_ANIMATION_DURATION));
        return fadeTransition;
    }

    @Override
    public boolean visible() {
        return true;
    }
}
