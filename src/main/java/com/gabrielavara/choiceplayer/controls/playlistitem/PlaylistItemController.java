package com.gabrielavara.choiceplayer.controls.playlistitem;

import static com.gabrielavara.choiceplayer.Constants.SHORT_ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.controls.playlistitem.PlaylistItemState.SELECTED;
import static java.util.Arrays.asList;

import java.net.URL;
import java.util.ResourceBundle;

import com.gabrielavara.choiceplayer.ChoicePlayerApplication;
import com.gabrielavara.choiceplayer.controls.albumart.AlbumArt;
import com.gabrielavara.choiceplayer.settings.AccentColor;
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

    private Color accentColor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        root.hoverProperty().addListener((ov, oldValue, newValue) -> albumArt.hover(newValue));
        AccentColor ac = ChoicePlayerApplication.getSettings().getTheme().getAccentColor();
        accentColor = new Color(((double) ac.getRed()) / 255, ((double) ac.getGreen()) / 255, ((double) ac.getBlue()) / 255, 1.0);
        indicator.setFill(accentColor);
    }

    public void setState(PlaylistItemState state) {
        double height = root.getHeight();
        Timeline indicatorTimeLine = createIndicatorTimeLine(state, height);
        for (Label label : asList(indexLabel, artistLabel, titleLabel, lengthLabel)) {
            Transition transition = createColorTransition(state, label);
            transition.play();
        }
        indicatorTimeLine.play();
    }

    private Timeline createIndicatorTimeLine(PlaylistItemState state, double height) {
        return new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(indicator.heightProperty(), state == SELECTED ? 0d : height)),
                new KeyFrame(Duration.millis(SHORT_ANIMATION_DURATION), new KeyValue(indicator.heightProperty(), state == SELECTED ? height : 0d)));
    }

    private Transition createColorTransition(PlaylistItemState state, Label label) {
        Color from = state == SELECTED ? (Color) label.getTextFill() : accentColor;
        Color to = state == SELECTED ? accentColor : (Color) label.getTextFill();
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
}
