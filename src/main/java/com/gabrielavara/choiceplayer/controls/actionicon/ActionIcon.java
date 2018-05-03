package com.gabrielavara.choiceplayer.controls.actionicon;

import static com.gabrielavara.choiceplayer.Constants.LONG_ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.controls.AnimationDirection.IN;
import static com.gabrielavara.choiceplayer.controls.AnimationDirection.OUT;
import static com.gabrielavara.choiceplayer.controls.CustomStage.StageLocation.CENTER;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gabrielavara.choiceplayer.controls.AnimationDirection;
import com.gabrielavara.choiceplayer.controls.CustomStage;
import com.gabrielavara.choiceplayer.settings.ColorConverter;
import com.gabrielavara.choiceplayer.settings.ThemeStyle;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class ActionIcon {
    private static final String COULD_NOT_LOAD = "Could not load action icon";
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.controls.toast.ActionIcon");

    @FXML
    private StackPane root;
    @FXML
    private MaterialDesignIconView iconView;

    private CustomStage stage;
    private ParallelTransition parallelTransition;

    public ActionIcon() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/controls/action_icon.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.load();
            initStage();
        } catch (IOException e) {
            log.error(COULD_NOT_LOAD);
            throw new IllegalStateException(COULD_NOT_LOAD, e);
        }
    }

    private void initStage() {
        root.setBackground(Background.EMPTY);
        stage = new CustomStage(root, CENTER);
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
    }

    public void showAndDismiss(Action action) {
        iconView.setIcon(action.getIcon());
        Color foregroundBrightColor = ColorConverter.convert(ThemeStyle.DARK.getForegroundBrightColor());
        iconView.setFill(foregroundBrightColor);
        stage.show();

        if (parallelTransition != null) {
            parallelTransition.playFromStart();
        } else {
            parallelTransition = new ParallelTransition(getScaleXTransition(), getScaleYTransition(), getSequentialFadeTransition());
            parallelTransition.setOnFinished(e -> stage.hide());
            parallelTransition.play();
        }
    }

    private ScaleTransition getScaleXTransition() {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(LONG_ANIMATION_DURATION), root);
        scaleTransition.setFromX(0.1);
        scaleTransition.setToX(1);
        return scaleTransition;
    }

    private ScaleTransition getScaleYTransition() {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(LONG_ANIMATION_DURATION), root);
        scaleTransition.setFromY(0.1);
        scaleTransition.setToY(1);
        return scaleTransition;
    }

    private SequentialTransition getSequentialFadeTransition() {
        FadeTransition fadeInTransition = getFadeTransition(IN);
        FadeTransition fadeOutTransition = getFadeTransition(OUT);
        return new SequentialTransition(fadeInTransition, fadeOutTransition);
    }

    private FadeTransition getFadeTransition(AnimationDirection animationDirection) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(LONG_ANIMATION_DURATION / 2), root);
        fadeTransition.setFromValue(animationDirection == IN ? 0 : 1);
        fadeTransition.setToValue(animationDirection == IN ? 1 : 0);
        return fadeTransition;
    }
}
