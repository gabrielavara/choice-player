package com.gabrielavara.choiceplayer.views;

import static com.gabrielavara.choiceplayer.Constants.DEFAULT_ALBUM_ART;
import static de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon.THUMB_DOWN;
import static de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon.THUMB_UP;
import static javafx.geometry.Pos.CENTER;

import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.felixroske.jfxsupport.SplashScreen;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class ChoicePlayerSplashScreen extends SplashScreen {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.views.ChoicePlayerSplashScreen");

    private ImageView imageView;

    @Override
    public Parent getParent() {
        loadImage();

        MaterialDesignIconView like = new MaterialDesignIconView(THUMB_UP);
        like.setFill(new Color(1, 1, 1, 1));
        like.setSize("36");
        StackPane likeStackPane = new StackPane();
        likeStackPane.setAlignment(CENTER);
        likeStackPane.getChildren().add(like);
        StackPane.setMargin(like, new Insets(6, 6, 6, 6));

        MaterialDesignIconView disLike = new MaterialDesignIconView(THUMB_DOWN);
        disLike.setFill(new Color(1, 1, 1, 1));
        disLike.setSize("36");
        StackPane disLikeStackPane = new StackPane();
        disLikeStackPane.setAlignment(CENTER);
        disLikeStackPane.getChildren().add(disLike);
        StackPane.setMargin(disLike, new Insets(6, 6, 6, 6));

        StackPane imageStackPane = new StackPane();
        imageStackPane.setAlignment(CENTER);
        imageStackPane.getChildren().add(imageView);

        HBox hBox = new HBox();
        hBox.getChildren().addAll(likeStackPane, imageStackPane, disLikeStackPane);

        hBox.setBackground(new Background(new BackgroundFill(new Color(0.235, 0.235, 0.235, 1), null, null)));
        int size = (36 + 75 + 12) * 2;
        hBox.setMinHeight(size);
        hBox.setMinWidth(size);

        return hBox;
    }

    private void loadImage() {
        try (FileInputStream inputStream = new FileInputStream(DEFAULT_ALBUM_ART)) {
            Image image = new Image(inputStream, 150, 150, true, false);
            imageView = new ImageView(image);
        } catch (IOException e) {
            log.error("Could not load default image");
        }
    }

    @Override
    public boolean visible() {
        return false;
    }
}
