package com.gabrielavara.choiceplayer.controls.animatedalbumart;

import static com.gabrielavara.choiceplayer.Constants.ALMOST_TOTALLY_HIDDEN;
import static com.gabrielavara.choiceplayer.Constants.SHORT_ANIMATION_DURATION;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class AnimatedAlbumArtController implements Initializable {
    @FXML
    public ImageView albumArt;
    @FXML
    public JFXButton playButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playButton.setOpacity(0);
        playButton.setTranslateY(50);
        albumArt.setCache(true);
    }

    public void setImage(Image image) {
        albumArt.setOpacity(ALMOST_TOTALLY_HIDDEN);
        albumArt.setImage(image);
        animate();
    }

    private void animate() {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(SHORT_ANIMATION_DURATION), albumArt);
        fadeTransition.setFromValue(ALMOST_TOTALLY_HIDDEN);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }

    public void hover(boolean hover) {
        FadeTransition buttonFadeTransition = new FadeTransition(Duration.millis(SHORT_ANIMATION_DURATION / 2), playButton);
        buttonFadeTransition.setFromValue(playButton.getOpacity());
        buttonFadeTransition.setToValue(hover ? 1 : 0);

        TranslateTransition buttonTranslateTransition = new TranslateTransition(Duration.millis(SHORT_ANIMATION_DURATION / 2), playButton);
        buttonTranslateTransition.setFromY(playButton.getTranslateY());
        buttonTranslateTransition.setToY(hover ? 0 : 50);

        FadeTransition albumArtFadeTransition = new FadeTransition(Duration.millis(SHORT_ANIMATION_DURATION / 2), albumArt);
        albumArtFadeTransition.setFromValue(albumArt.getOpacity());
        albumArtFadeTransition.setToValue(hover ? 0.25 : 1);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(buttonFadeTransition, buttonTranslateTransition, albumArtFadeTransition);
        parallelTransition.play();
    }
}
