package com.gabrielavara.choiceplayer.controls.toast;

import static com.gabrielavara.choiceplayer.Constants.ALBUM_ART_SIZE;
import static com.gabrielavara.choiceplayer.Constants.ANIMATION_DURATION;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gabrielavara.choiceplayer.controls.albumart.AlbumArt;
import com.gabrielavara.choiceplayer.dto.Mp3;
import com.gabrielavara.choiceplayer.util.ImageUtil;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
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
    private AnchorPane root;

    private boolean isShowed;
    private ToastStage stage;
    private SequentialTransition sequentialTransition;
    private Timeline showAnimation;
    private Timeline dismissAnimation;

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
        stage = new ToastStage(root);
        stage.setScene(new Scene(root));
        stage.setAlwaysOnTop(true);
        stage.setLocation(stage.getBottomRight());
        stage.show();

        albumArt.setHoverAllowed(false);

        showAnimation = setupShowAnimation();
        dismissAnimation = setupDismissAnimation();
        sequentialTransition = new SequentialTransition(showAnimation, dismissAnimation);
    }

    public void setItems(Mp3 mp3) {
        artistLabel.setText(mp3.getArtist());
        titleLabel.setText(mp3.getTitle());

        Optional<byte[]> albumArtData = mp3.getAlbumArt();
        Image image = ImageUtil.getAlbumArt(albumArtData, ALBUM_ART_SIZE);
        albumArt.setImage(image);
    }

    private Timeline setupShowAnimation() {

        Timeline tl = new Timeline();

        double offScreenX = stage.getOffScreenBounds().getX();
        KeyValue kvX = new KeyValue(stage.xLocationProperty(), offScreenX);
        KeyFrame frame1 = new KeyFrame(Duration.ZERO, kvX);

        KeyValue kvInter = new KeyValue(stage.xLocationProperty(), stage.getBottomRight().getX());
        KeyFrame frame2 = new KeyFrame(Duration.millis(ANIMATION_DURATION), kvInter);

        KeyValue kvOpacity = new KeyValue(stage.opacityProperty(), 0.0);
        KeyFrame frame3 = new KeyFrame(Duration.ZERO, kvOpacity);

        KeyValue kvOpacity2 = new KeyValue(stage.opacityProperty(), 1.0);
        KeyFrame frame4 = new KeyFrame(Duration.millis(ANIMATION_DURATION), kvOpacity2);

        tl.getKeyFrames().addAll(frame1, frame2, frame3, frame4);

        tl.setOnFinished(e -> isShowed = true);

        return tl;
    }

    private Timeline setupDismissAnimation() {

        Timeline tl = new Timeline();

        double offScreenX = stage.getOffScreenBounds().getX();
        double trayPadding = 3;

        KeyValue kvX = new KeyValue(stage.xLocationProperty(), offScreenX + trayPadding);
        KeyFrame frame1 = new KeyFrame(Duration.millis(ANIMATION_DURATION), kvX);

        KeyValue kvOpacity = new KeyValue(stage.opacityProperty(), 0.4);
        KeyFrame frame2 = new KeyFrame(Duration.millis(ANIMATION_DURATION), kvOpacity);

        tl.getKeyFrames().addAll(frame1, frame2);

        tl.setOnFinished(e -> {
            isShowed = false;
            stage.close();
            stage.setLocation(stage.getBottomRight());
        });

        return tl;
    }

    public void showAndDismiss() {
        if (isShowed) {
            dismiss();
        } else {
            stage.show();
            playSequential();
        }
    }

    private void dismiss() {
        if (isShowed) {
            playDismissAnimation();
        }
    }

    private void playDismissAnimation() {
        dismissAnimation.play();
    }

    private void playSequential() {
        sequentialTransition.getChildren().get(1).setDelay(Duration.seconds(3));
        sequentialTransition.play();
    }

}
