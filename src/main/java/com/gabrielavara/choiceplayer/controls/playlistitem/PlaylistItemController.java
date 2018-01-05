package com.gabrielavara.choiceplayer.controls.playlistitem;

import static com.gabrielavara.choiceplayer.Constants.ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.controls.playlistitem.PlaylistItemState.SELECTED;
import static java.util.Arrays.asList;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.gabrielavara.choiceplayer.ChoicePlayerApplication;
import com.gabrielavara.choiceplayer.controls.albumart.AlbumArt;
import com.jfoenix.controls.JFXRippler;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import lombok.Getter;

public class PlaylistItemController implements Initializable {
    @FXML
    public StackPane root;
    @FXML
    public HBox hBox;
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
    public Rectangle indicator;

    private boolean isAnimating;
    private static double height;
    private List<Label> labels;
    private JFXRippler rippler;

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
            for (Label label : labels) {
                label.setTextFill(ChoicePlayerApplication.getColors().getAccentColor());
            }
        } else {
            indicator.setHeight(0);
            setLabelColors();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        indicator.setFill(ChoicePlayerApplication.getColors().getAccentColor());

        labels = asList(indexLabel, artistLabel, titleLabel, lengthLabel);
        setLabelColors();

        rippler = new JFXRippler(hBox);
        rippler.setRipplerFill(ChoicePlayerApplication.getColors().getAccentColor());
        root.getChildren().add(rippler);

        root.hoverProperty().addListener((ov, oldValue, newValue) -> albumArt.hover(newValue));
    }

    private void setLabelColors() {
        indexLabel.setTextFill(ChoicePlayerApplication.getColors().getForegroundColor());
        artistLabel.setTextFill(ChoicePlayerApplication.getColors().getForegroundBrightColor());
        titleLabel.setTextFill(ChoicePlayerApplication.getColors().getForegroundColor());
        lengthLabel.setTextFill(ChoicePlayerApplication.getColors().getForegroundBrightColor());
    }

    public void animateToState(PlaylistItemState state) {
        isAnimating = true;
        setHeight(root);
        animateLabels(state);
        animateIndicator(state);
    }

    private static void setHeight(Pane root) {
        if (height < 1) {
            height = root.getHeight();
        }
    }

    private void animateLabels(PlaylistItemState state) {
        for (Label label : labels) {
            Transition transition = createColorTransition(state, label);
            transition.play();
        }
    }

    private Transition createColorTransition(PlaylistItemState state, Label label) {
        Color from = (Color) label.getTextFill();
        Color color = label.equals(titleLabel)
                ? ChoicePlayerApplication.getColors().getForegroundColor()
                : ChoicePlayerApplication.getColors().getForegroundBrightColor();
        Color to = state == SELECTED ? ChoicePlayerApplication.getColors().getAccentColor() : color;
        return new ColorTransition(label, from, to);
    }

    private void animateIndicator(PlaylistItemState state) {
        Timeline indicatorTimeLine = createIndicatorTimeLine(state);
        indicatorTimeLine.setOnFinished(e -> isAnimating = false);
        indicatorTimeLine.play();
    }

    private Timeline createIndicatorTimeLine(PlaylistItemState state) {
        return new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(indicator.heightProperty(), state == SELECTED ? 0d : height)),
                new KeyFrame(Duration.millis(ANIMATION_DURATION), new KeyValue(indicator.heightProperty(), state == SELECTED ? height : 0d)));
    }

    public void changeTheme() {
        indicator.setFill(ChoicePlayerApplication.getColors().getAccentColor());
        rippler.setRipplerFill(ChoicePlayerApplication.getColors().getAccentColor());
        setLabelColors();
    }
}
