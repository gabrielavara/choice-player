package com.gabrielavara.choiceplayer.controls.albumart;

import static com.gabrielavara.choiceplayer.Constants.ALBUM_ART_TRANSLATE_X;
import static com.gabrielavara.choiceplayer.Constants.SHORT_ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.views.QuadraticInterpolator.QUADRATIC_EASE_OUT;

import java.net.URL;
import java.util.ResourceBundle;

import com.gabrielavara.choiceplayer.ChoicePlayerApplication;
import com.jfoenix.controls.JFXButton;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class AlbumArtController implements Initializable {
    @FXML
    public ImageView albumArt;
    @FXML
    public JFXButton playButton;
    @FXML
    public AnchorPane pane;

    private FadeTransition fadeTransition;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playButton.setOpacity(0);
        playButton.setTranslateX(-ALBUM_ART_TRANSLATE_X);
        albumArt.setCache(true);
        playButton.setMouseTransparent(true);
    }

    public void setImage(Image image) {
        albumArt.setOpacity(0);
        albumArt.setImage(image);
        animate();
    }

    private void animate() {
        fadeTransition = new FadeTransition(Duration.millis(SHORT_ANIMATION_DURATION), albumArt);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.setInterpolator(QUADRATIC_EASE_OUT);
        fadeTransition.setOnFinished(e -> {
            setBackground();
            fadeTransition = null;
        });
        fadeTransition.play();
    }

    void hover(boolean hover) {
        FadeTransition buttonFadeTransition = new FadeTransition(Duration.millis(SHORT_ANIMATION_DURATION), playButton);
        buttonFadeTransition.setFromValue(playButton.getOpacity());
        buttonFadeTransition.setToValue(hover ? 1 : 0);
        buttonFadeTransition.setInterpolator(QUADRATIC_EASE_OUT);

        TranslateTransition buttonTranslateTransition = new TranslateTransition(Duration.millis(SHORT_ANIMATION_DURATION), playButton);
        buttonTranslateTransition.setFromX(playButton.getTranslateX());
        buttonTranslateTransition.setToX(hover ? 0 : -ALBUM_ART_TRANSLATE_X);
        buttonTranslateTransition.setInterpolator(QUADRATIC_EASE_OUT);

        FadeTransition albumArtFadeTransition = new FadeTransition(Duration.millis(SHORT_ANIMATION_DURATION), albumArt);
        albumArtFadeTransition.setFromValue(albumArt.getOpacity());
        albumArtFadeTransition.setToValue(hover ? 0.25 : 1);
        albumArtFadeTransition.setInterpolator(QUADRATIC_EASE_OUT);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(buttonFadeTransition, buttonTranslateTransition, albumArtFadeTransition);
        parallelTransition.play();
        stopFadeTransition();
    }

    private void stopFadeTransition() {
        if (fadeTransition != null) {
            fadeTransition.stop();
            fadeTransition = null;
        }
    }

    private void setBackground() {
        Color absoluteColor = ChoicePlayerApplication.getColors().getAbsoluteColor();
        pane.setBackground(new Background(new BackgroundFill(absoluteColor, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    void setTheme() {
        setBackground();
    }
}
