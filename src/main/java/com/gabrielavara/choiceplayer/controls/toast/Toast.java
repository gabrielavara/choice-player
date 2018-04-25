package com.gabrielavara.choiceplayer.controls.toast;

import static com.gabrielavara.choiceplayer.Constants.ALBUM_ART_SIZE;
import static com.gabrielavara.choiceplayer.Constants.ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.Constants.TOAST_TIMEOUT;
import static com.gabrielavara.choiceplayer.views.QuadraticInterpolator.QUADRATIC_EASE_IN;
import static com.gabrielavara.choiceplayer.views.QuadraticInterpolator.QUADRATIC_EASE_OUT;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gabrielavara.choiceplayer.ChoicePlayerApplication;
import com.gabrielavara.choiceplayer.controls.CustomStage;
import com.gabrielavara.choiceplayer.controls.albumart.AlbumArt;
import com.gabrielavara.choiceplayer.dto.Mp3;
import com.gabrielavara.choiceplayer.util.CssModifier;
import com.gabrielavara.choiceplayer.util.ImageUtil;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class Toast {
    private static final String COULD_NOT_LOAD = "Could not load toast";
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.controls.toast.Toast");

    @FXML
    public HBox hBox;
    @FXML
    public AlbumArt albumArt;
    @FXML
    public Label artistLabel;
    @FXML
    public Label titleLabel;
    @FXML
    private StackPane root;

    private boolean isShowed;
    private CustomStage stage;
    private TranslateTransition inTransition;
    private PauseTransition wait;
    private TranslateTransition outTransition;

    public Toast() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/controls/toast.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.load();
            initStage();
        } catch (IOException e) {
            log.error(COULD_NOT_LOAD);
            throw new IllegalStateException(COULD_NOT_LOAD, e);
        }
    }

    private void initStage() {
        CssModifier.modify(root);
        stage = new CustomStage(root);
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.setLocation(stage.getBottomRight());

        albumArt.setHoverAllowed(false);
    }

    public void setItems(Mp3 mp3) {
        artistLabel.setText(mp3.getArtist());
        titleLabel.setText(mp3.getTitle());
        artistLabel.setTextFill(ChoicePlayerApplication.getColors().getForegroundBrightColor());
        titleLabel.setTextFill(ChoicePlayerApplication.getColors().getForegroundColor());

        Optional<byte[]> albumArtData = mp3.getAlbumArt();
        Image image = ImageUtil.getAlbumArt(albumArtData, ALBUM_ART_SIZE);
        albumArt.setImage(image);
    }

    public void showAndDismiss() {
        root.setTranslateX(0);
        stage.show();

        if (inTransition != null) {
            stopTransitions();
            inTransition.playFromStart();
        } else {
            createInTransition();
            inTransition.play();
        }
        isShowed = !isShowed;
    }

    private void stopTransitions() {
        if (wait != null) {
            wait.stop();
        }
        if (outTransition != null) {
            outTransition.stop();
        }
    }

    private void createInTransition() {
        inTransition = getTranslateTransition();
        inTransition.setOnFinished(e -> {
            if (wait != null) {
                wait.playFromStart();
            } else {
                wait = new PauseTransition(Duration.millis(TOAST_TIMEOUT));
                wait.setOnFinished(we -> dismiss());
                wait.play();
            }
        });
    }

    private void dismiss() {
        if (!isShowed) {
            return;
        }

        if (outTransition != null) {
            outTransition.playFromStart();
        } else {
            outTransition = getTranslateTransition();
            outTransition.setOnFinished(e -> stage.hide());
            outTransition.play();
        }
        isShowed = !isShowed;
    }

    private TranslateTransition getTranslateTransition() {
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(ANIMATION_DURATION), root);
        translateTransition.setFromX(isShowed ? 0 : root.getWidth());
        translateTransition.setToX(isShowed ? root.getWidth() : 0);
        translateTransition.setInterpolator(isShowed ? QUADRATIC_EASE_IN : QUADRATIC_EASE_OUT);
        return translateTransition;
    }

}
