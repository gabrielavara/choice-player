package com.gabrielavara.choiceplayer.controls.settings;

import static java.util.Arrays.asList;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gabrielavara.choiceplayer.ChoicePlayerApplication;
import com.gabrielavara.choiceplayer.messages.SettingsClosedMessage;
import com.gabrielavara.choiceplayer.settings.ColorConverter;
import com.gabrielavara.choiceplayer.util.Messenger;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXComboBox;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;

public class SettingsController implements Initializable {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.controls.settings.SettingsController");

    @FXML
    public Label folderToLoadLabel;
    @FXML
    public Label folderToMoveLikedMusicLabel;
    @FXML
    public JFXComboBox<String> styleComboBox;
    @FXML
    public JFXColorPicker accentColorPicker;
    @FXML
    public JFXButton folderToLoadBrowseButton;
    @FXML
    public JFXButton folderToMoveLikedMusicBrowseButton;
    @FXML
    public AnchorPane titleContainer;
    @FXML
    public Label titleLabel;
    @FXML
    public JFXButton closeButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("language.player");
        folderToLoadLabel.setText(ChoicePlayerApplication.getSettings().getFolder());
        folderToMoveLikedMusicLabel.setText(ChoicePlayerApplication.getSettings().getLikedFolder());
        accentColorPicker.setValue(ChoicePlayerApplication.getColors().getAccentColor());
        ObservableList<String> styles = FXCollections
                        .observableList(asList(resourceBundle.getString("settingsStyleLight"), resourceBundle.getString("settingsStyleDark")));
        styleComboBox.setItems(styles);
        titleLabel.translateXProperty().bind(titleContainer.widthProperty().subtract(titleLabel.widthProperty()).divide(2));
        titleLabel.translateYProperty().bind(titleContainer.heightProperty().subtract(titleLabel.heightProperty()).divide(2));
        closeButton.setOnMouseClicked(e -> Messenger.send(new SettingsClosedMessage()));
    }

    @FXML
    public void folderToLoadBrowseButtonClicked(MouseEvent mouseEvent) {
        showDirectoryChooser(folderToLoadLabel, value -> ChoicePlayerApplication.getSettings().setFolder(value));
    }

    @FXML
    public void folderToMoveLikedMusicBrowseButtonClicked(MouseEvent mouseEvent) {
        showDirectoryChooser(folderToMoveLikedMusicLabel, value -> ChoicePlayerApplication.getSettings().setLikedFolder(value));
    }

    private void showDirectoryChooser(Label label, SettingsSetter settingsSetter) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(Paths.get(label.getText()).toFile());
        File selectedDir = directoryChooser.showDialog(ChoicePlayerApplication.getStage());
        if (selectedDir == null) {
            log.info("No directory selected");
        } else {
            label.setText(selectedDir.getAbsolutePath());
            settingsSetter.set(selectedDir.getAbsolutePath());
        }
    }

    @FXML
    public void accentColorPickerChanged(ActionEvent actionEvent) {
        ChoicePlayerApplication.getSettings().getTheme().setAccentColor(ColorConverter.convert(accentColorPicker.getValue()));
    }

    private interface SettingsSetter {
        void set(String value);
    }
}
