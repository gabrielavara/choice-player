package com.gabrielavara.choiceplayer.controls.toast;

import static com.gabrielavara.choiceplayer.Constants.ALBUM_ART_SIZE;
import static com.gabrielavara.choiceplayer.Constants.SHORT_ANIMATION_DURATION;
import static com.gabrielavara.choiceplayer.views.QuadraticInterpolator.QUADRATIC_EASE_OUT;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gabrielavara.choiceplayer.controls.albumart.AlbumArt;
import com.gabrielavara.choiceplayer.dto.Mp3;
import com.gabrielavara.choiceplayer.util.ImageUtil;

import javafx.animation.TranslateTransition;
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

    public Toast(Mp3 mp3) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/controls/toast.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.load();

            initStage();
            init(mp3);
        } catch (IOException e) {
            log.error(COULD_NOT_LOAD);
            throw new IllegalStateException(COULD_NOT_LOAD, e);
        }
    }

    private void initStage() {
        ToastStage stage = new ToastStage(root);
        stage.setScene(new Scene(root));
        stage.setAlwaysOnTop(true);
        stage.setLocation(stage.getBottomRight());

        albumArt.setHoverAllowed(false);
    }

    private void init(Mp3 mp3) {
        artistLabel.setText(mp3.getArtist());
        titleLabel.setText(mp3.getTitle());

        Optional<byte[]> albumArtData = mp3.getAlbumArt();
        Image image = ImageUtil.getAlbumArt(albumArtData, ALBUM_ART_SIZE);
        this.albumArt.setImage(image);
    }

    public void showAndDismiss() {
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(SHORT_ANIMATION_DURATION), root);
        translateTransition.setFromX(root.getTranslateX());
        translateTransition.setToX(isShowed ? 0 : -root.getPrefWidth());
        translateTransition.setInterpolator(QUADRATIC_EASE_OUT);
        translateTransition.play();
        isShowed = !isShowed;
    }

}
