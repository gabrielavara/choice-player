package com.gabrielavara.choiceplayer.controls.animatedbadge;

import com.gabrielavara.choiceplayer.controls.AnimationDirection;
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

import java.net.URL;
import java.util.ResourceBundle;

import static com.gabrielavara.choiceplayer.Constants.BADGE_MAX_SCALE;
import static com.gabrielavara.choiceplayer.Constants.BADGE_MIN_SCALE;
import static com.gabrielavara.choiceplayer.Constants.BADGE_SIZE;
import static com.gabrielavara.choiceplayer.Constants.BADGE_VISIBILITY_SECONDS;
import static com.gabrielavara.choiceplayer.Constants.SHORT_ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.Constants.VERY_SHORT_ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.controls.AnimationDirection.IN;
import static com.gabrielavara.choiceplayer.controls.AnimationDirection.OUT;
import static com.gabrielavara.choiceplayer.views.QuadraticInterpolator.QUADRATIC_EASE_BOTH;
import static com.gabrielavara.choiceplayer.views.QuadraticInterpolator.QUADRATIC_EASE_IN;
import static com.gabrielavara.choiceplayer.views.QuadraticInterpolator.QUADRATIC_EASE_OUT;
import static javafx.geometry.Pos.CENTER;

public class AnimatedBadgeController implements Initializable {
    @FXML
    public Label badgeLabel;
    private StackPane rootContainer;
    private Button toDecorate;
    private int count;
    private double x;
    private double y;

    AnimatedBadgeController(StackPane rootContainer, Button toDecorate) {
        this.rootContainer = rootContainer;
        this.toDecorate = toDecorate;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        badgeLabel.setOpacity(0);
        badgeLabel.setText(String.valueOf(count));
        Point2D toDecoratePosition = toDecorate.localToScene(new Point2D(0, 0));
        x = toDecoratePosition.getX() + toDecorate.getWidth() - BADGE_SIZE / 2;
        y = toDecoratePosition.getY() - BADGE_SIZE / 2;
        rootContainer.getChildren().add(badgeLabel);
        badgeLabel.setMinSize(BADGE_SIZE, BADGE_SIZE);
        badgeLabel.setAlignment(CENTER);
        badgeLabel.setMouseTransparent(true);
        toDecorate.hoverProperty().addListener((ov, oldValue, newValue) -> {
            setLocationIfNeeded();
            createTimeLine(newValue ? IN : OUT).play();
        });
    }

    void increaseCount() {
        badgeLabel.setText(String.valueOf(++count));
        setLocationIfNeeded();
        animate();
    }

    private void animate() {
        Timeline pop = pop();
        pop.setOnFinished(e -> {
            PauseTransition pause = new PauseTransition(Duration.seconds(BADGE_VISIBILITY_SECONDS));
            pause.setOnFinished(ev -> createTimeLine(OUT).play());
            pause.play();
        });
        pop.play();
    }

    private Timeline pop() {
        return new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(badgeLabel.scaleXProperty(), BADGE_MIN_SCALE),
                        new KeyValue(badgeLabel.scaleYProperty(), BADGE_MIN_SCALE),
                        new KeyValue(badgeLabel.opacityProperty(), 0)),
                new KeyFrame(Duration.millis(SHORT_ANIMATION_DURATION),
                        new KeyValue(badgeLabel.scaleXProperty(), BADGE_MAX_SCALE, QUADRATIC_EASE_IN),
                        new KeyValue(badgeLabel.scaleYProperty(), BADGE_MAX_SCALE, QUADRATIC_EASE_IN),
                        new KeyValue(badgeLabel.opacityProperty(), 1)),
                new KeyFrame(Duration.millis(SHORT_ANIMATION_DURATION + VERY_SHORT_ANIMATION_DURATION),
                        new KeyValue(badgeLabel.scaleXProperty(), 1, QUADRATIC_EASE_OUT),
                        new KeyValue(badgeLabel.scaleYProperty(), 1, QUADRATIC_EASE_OUT),
                        new KeyValue(badgeLabel.opacityProperty(), 1)));
    }

    private Timeline createTimeLine(AnimationDirection direction) {
        return new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(badgeLabel.scaleXProperty(), badgeLabel.getScaleX()),
                        new KeyValue(badgeLabel.scaleYProperty(), badgeLabel.getScaleY()),
                        new KeyValue(badgeLabel.opacityProperty(), badgeLabel.getOpacity())),
                new KeyFrame(Duration.millis(SHORT_ANIMATION_DURATION),
                        new KeyValue(badgeLabel.scaleXProperty(), direction == IN ? 1 : BADGE_MIN_SCALE, QUADRATIC_EASE_BOTH),
                        new KeyValue(badgeLabel.scaleYProperty(), direction == IN ? 1 : BADGE_MIN_SCALE, QUADRATIC_EASE_BOTH),
                        new KeyValue(badgeLabel.opacityProperty(), direction == IN ? 1 : 0)));
    }

    private void setLocationIfNeeded() {
        if (badgeLabel.isManaged()) {
            badgeLabel.setManaged(false);
            badgeLabel.relocate(x, y);
        }
    }
}
