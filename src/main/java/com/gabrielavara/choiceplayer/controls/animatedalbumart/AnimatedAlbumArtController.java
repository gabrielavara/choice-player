package com.gabrielavara.choiceplayer.controls.animatedalbumart;

import static com.gabrielavara.choiceplayer.Constants.ALMOST_TOTALLY_HIDDEN;
import static com.gabrielavara.choiceplayer.Constants.ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.Constants.SHORT_ANIMATION_DURATION;

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

public class AnimatedAlbumArtController implements Initializable {
    private static final int TRANSLATE_X = 20;
    @FXML
    public ImageView albumArt;
    @FXML
    public JFXButton playButton;
    @FXML
    public AnchorPane pane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playButton.setOpacity(0);
        playButton.setTranslateX(-TRANSLATE_X);
        albumArt.setCache(true);
    }

    public void setImage(Image image) {
        albumArt.setOpacity(ALMOST_TOTALLY_HIDDEN);
        albumArt.setImage(image);
        animate();
    }

    private void animate() {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(ANIMATION_DURATION), albumArt);
        fadeTransition.setFromValue(ALMOST_TOTALLY_HIDDEN);
        fadeTransition.setToValue(1);
        fadeTransition.setOnFinished(e -> {
            Color color = ChoicePlayerApplication.getSettings().getTheme().getStyle().getAbsoluteColor();
            pane.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        });
        fadeTransition.play();
    }

    public void hover(boolean hover) {
        FadeTransition buttonFadeTransition = new FadeTransition(Duration.millis(SHORT_ANIMATION_DURATION), playButton);
        buttonFadeTransition.setFromValue(playButton.getOpacity());
        buttonFadeTransition.setToValue(hover ? 1 : 0);

        TranslateTransition buttonTranslateTransition = new TranslateTransition(Duration.millis(SHORT_ANIMATION_DURATION), playButton);
        buttonTranslateTransition.setFromX(playButton.getTranslateX());
        buttonTranslateTransition.setToX(hover ? 0 : -TRANSLATE_X);

        FadeTransition albumArtFadeTransition = new FadeTransition(Duration.millis(SHORT_ANIMATION_DURATION), albumArt);
        albumArtFadeTransition.setFromValue(albumArt.getOpacity());
        albumArtFadeTransition.setToValue(hover ? 0.25 : 1);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(buttonFadeTransition, buttonTranslateTransition, albumArtFadeTransition);
        parallelTransition.play();
    }
}
