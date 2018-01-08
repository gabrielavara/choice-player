package com.gabrielavara.choiceplayer.controls.animatedbadge;

import static com.gabrielavara.choiceplayer.Constants.ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.Constants.BADGE_MAX_SCALE;
import static com.gabrielavara.choiceplayer.Constants.BADGE_MIN_SCALE;
import static com.gabrielavara.choiceplayer.Constants.SHORT_ANIMATION_DURATION;
import static javafx.animation.Interpolator.EASE_IN;
import static javafx.animation.Interpolator.EASE_OUT;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class AnimatedBadgeController implements Initializable {
    @FXML
    public Label badgeLabel;
    private StackPane rootContainer;
    private Button toDecorate;
    private int count;

    AnimatedBadgeController(StackPane rootContainer, Button toDecorate) {
        this.rootContainer = rootContainer;
        this.toDecorate = toDecorate;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        badgeLabel.setManaged(false);
        badgeLabel.setOpacity(0);
        Point2D toDecoratePosition = toDecorate.localToScene(new Point2D(0, 0));
        double x = toDecoratePosition.getX() + toDecorate.getWidth() - badgeLabel.getWidth() / 2;
        double y = toDecoratePosition.getY() + badgeLabel.getHeight() / 2;
        badgeLabel.relocate(x, y);
        rootContainer.getChildren().add(badgeLabel);
    }

    public void increaseCount() {
        badgeLabel.setText(String.valueOf(++count));
        Timeline badgeTimeLineIn = createBadgeTimeLineIn();
        badgeTimeLineIn.setOnFinished(e -> {
            PauseTransition pauseTransition = new PauseTransition(Duration.seconds(5));
            pauseTransition.setOnFinished(ev -> createBadgeTimeLineOut().play());
        });
        badgeTimeLineIn.play();
    }

    private Timeline createBadgeTimeLineIn() {
        return new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(badgeLabel.scaleXProperty(), BADGE_MIN_SCALE),
                                        new KeyValue(badgeLabel.scaleYProperty(), BADGE_MIN_SCALE), new KeyValue(badgeLabel.opacityProperty(), 0)),
                        new KeyFrame(Duration.millis(ANIMATION_DURATION), new KeyValue(badgeLabel.scaleXProperty(), BADGE_MAX_SCALE, EASE_IN),
                                        new KeyValue(badgeLabel.scaleYProperty(), BADGE_MAX_SCALE, EASE_IN), new KeyValue(badgeLabel.opacityProperty(), 1)),
                        new KeyFrame(Duration.millis(ANIMATION_DURATION + SHORT_ANIMATION_DURATION), new KeyValue(badgeLabel.scaleXProperty(), 1, EASE_OUT),
                                        new KeyValue(badgeLabel.scaleYProperty(), 1, EASE_OUT), new KeyValue(badgeLabel.opacityProperty(), 1)));
    }

    private Timeline createBadgeTimeLineOut() {
        return new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(badgeLabel.scaleXProperty(), 1), new KeyValue(badgeLabel.scaleYProperty(), 1),
                                        new KeyValue(badgeLabel.opacityProperty(), 1)),
                        new KeyFrame(Duration.millis(ANIMATION_DURATION), new KeyValue(badgeLabel.scaleXProperty(), BADGE_MIN_SCALE),
                                        new KeyValue(badgeLabel.scaleYProperty(), BADGE_MIN_SCALE), new KeyValue(badgeLabel.opacityProperty(), 0)));
    }
}
