package com.gabrielavara.choiceplayer.controls.playlistitem;

import static com.gabrielavara.choiceplayer.Constants.ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.controls.playlistitem.PlaylistItemState.SELECTED;
import static java.util.Arrays.asList;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.gabrielavara.choiceplayer.ChoicePlayerApplication;
import com.gabrielavara.choiceplayer.controls.albumart.AlbumArt;
import com.gabrielavara.choiceplayer.settings.ColorConverter;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import lombok.Getter;

public class PlaylistItemController implements Initializable {
    @FXML
    public Label indexLabel;
    @FXML
    @Getter
    public AlbumArt albumArt;
    @FXML
    public Label artistLabel;
    @FXML
    public Label titleLabel;
    @FXML
    public Label lengthLabel;
    @FXML
    public HBox root;
    @FXML
    public Rectangle indicator;

    private boolean isAnimating;
    private static final Color accentColor;
    private static final Color foregroundBrightColor;
    private static double height;
    private List<Label> labels;

    static {
        accentColor = ColorConverter.convert(ChoicePlayerApplication.getSettings().getTheme().getAccentColor());
        foregroundBrightColor = ColorConverter.convert(ChoicePlayerApplication.getSettings().getTheme().getStyle().getForegroundBrightColor());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        root.hoverProperty().addListener((ov, oldValue, newValue) -> albumArt.hover(newValue));
        indicator.setFill(accentColor);
        labels = asList(indexLabel, artistLabel, titleLabel, lengthLabel);
        labels.forEach(l -> {
            l.getStyleClass().remove("label");
            l.setTextFill(foregroundBrightColor);
        });
    }

    public void animateToState(PlaylistItemState state) {
        isAnimating = true;
        setHeight(root);
        animateLabels(state);
        animateIndicator(state);
    }

    private void animateLabels(PlaylistItemState state) {
        for (Label label : labels) {
            Transition transition = createColorTransition(state, label);
            transition.play();
        }
    }

    private void animateIndicator(PlaylistItemState state) {
        Timeline indicatorTimeLine = createIndicatorTimeLine(state);
        indicatorTimeLine.setOnFinished(e -> isAnimating = false);
        indicatorTimeLine.play();
    }

    private static void setHeight(HBox root) {
        if (height < 1) {
            height = root.getHeight();
        }
    }

    private Timeline createIndicatorTimeLine(PlaylistItemState state) {
        return new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(indicator.heightProperty(), state == SELECTED ? 0d : height)),
                new KeyFrame(Duration.millis(ANIMATION_DURATION), new KeyValue(indicator.heightProperty(), state == SELECTED ? height : 0d)));
    }

    private Transition createColorTransition(PlaylistItemState state, Label label) {
        Color from = (Color) label.getTextFill();
        Color to = state == SELECTED ? accentColor : foregroundBrightColor;
        return new ColorTransition(label, from, to);
    }

    public void setIndex(String index) {
        indexLabel.setText(index);
    }

    public void setArtist(String artist) {
        artistLabel.setText(artist);
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void setLength(String length) {
        lengthLabel.setText(length);
    }

    public void setState(boolean currentlyPlaying) {
        if (currentlyPlaying && !isAnimating) {
            indicator.setHeight(height);
            for (Label label : asList(indexLabel, artistLabel, titleLabel, lengthLabel)) {
                label.setTextFill(accentColor);
            }
        }
    }
}
