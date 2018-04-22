package com.gabrielavara.choiceplayer.controls.overlay;

import java.net.URL;
import java.util.ResourceBundle;

import com.gabrielavara.choiceplayer.ChoicePlayerApplication;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class OverlayController implements Initializable {
    @FXML
    public StackPane root;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Color accentColor = ChoicePlayerApplication.getColors().getAccentColor();
        root.setBackground(new Background(new BackgroundFill(accentColor, null, null)));
    }
}
