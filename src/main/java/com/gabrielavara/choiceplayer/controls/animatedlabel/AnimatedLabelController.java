package com.gabrielavara.choiceplayer.controls.animatedlabel;

import static com.gabrielavara.choiceplayer.Constants.ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.Constants.LABEL_TRANSLATE_X;
import static com.gabrielavara.choiceplayer.views.QuadraticInterpolator.QUADRATIC_EASE_OUT;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class AnimatedLabelController implements Initializable {
    @FXML
    public Label first;
    @FXML
    public Label second;
    @FXML
    public StackPane root;

    private String text;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        first.setOpacity(0);
        first.setTranslateX(LABEL_TRANSLATE_X);
        second.setOpacity(0);
        second.setTranslateX(LABEL_TRANSLATE_X);
    }

    public void setStyleClass(String styleClass) {
        first.getStyleClass().add(styleClass);
        second.getStyleClass().add(styleClass);
    }

    public void setText(String text) {
        this.text = text;
        second.setText(text);
        second.toFront();
        animateOut(first);
        animateIn(second);
    }

    private void animateIn(Label label) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(ANIMATION_DURATION * 2), label);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.setInterpolator(QUADRATIC_EASE_OUT);

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(ANIMATION_DURATION * 2), label);
        translateTransition.setByX(-LABEL_TRANSLATE_X);
        translateTransition.setInterpolator(QUADRATIC_EASE_OUT);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(fadeTransition, translateTransition);

        parallelTransition.setOnFinished(event -> {
            Label temp = first;
            first = second;
            second = temp;
            second.setTranslateX(LABEL_TRANSLATE_X);
        });
        parallelTransition.play();
    }

    private void animateOut(Label label) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(ANIMATION_DURATION * 2), label);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.setInterpolator(QUADRATIC_EASE_OUT);

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(ANIMATION_DURATION * 2), label);
        translateTransition.setByX(-LABEL_TRANSLATE_X);
        translateTransition.setInterpolator(QUADRATIC_EASE_OUT);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(fadeTransition, translateTransition);

        parallelTransition.play();
    }

    public String getText() {
        return text;
    }

    public void setTextFill(Color textFill) {
        first.setTextFill(textFill);
        second.setTextFill(textFill);
    }

    public void setStackPaneAlignment(Pos pos) {
        StackPane.setAlignment(first, pos);
        StackPane.setAlignment(second, pos);
    }
}
