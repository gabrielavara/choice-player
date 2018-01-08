package com.gabrielavara.choiceplayer.controls.settings;

import static com.gabrielavara.choiceplayer.Constants.LONG_ANIMATION_DURATION;
import static java.util.Arrays.asList;
import static javafx.animation.Interpolator.EASE_BOTH;
import static javafx.geometry.Pos.CENTER_LEFT;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gabrielavara.choiceplayer.ChoicePlayerApplication;
import com.gabrielavara.choiceplayer.controls.animatedlabel.AnimatedLabel;
import com.gabrielavara.choiceplayer.messages.SettingsClosedMessage;
import com.gabrielavara.choiceplayer.messages.ThemeChangedMessage;
import com.gabrielavara.choiceplayer.settings.AccentColor;
import com.gabrielavara.choiceplayer.settings.ColorConverter;
import com.gabrielavara.choiceplayer.settings.ThemeStyle;
import com.gabrielavara.choiceplayer.util.Messenger;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXComboBox;

import javafx.animation.RotateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;

public class SettingsController implements Initializable {
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.controls.settings.SettingsController");

    @FXML
    public AnimatedLabel folderToLoadLabel;
    @FXML
    public AnimatedLabel folderToMoveLikedMusicLabel;
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
    public JFXButton backButton;

    private boolean folderChanged;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("language.player");
        folderToLoadLabel.setText(ChoicePlayerApplication.getSettings().getFolder());
        folderToMoveLikedMusicLabel.setText(ChoicePlayerApplication.getSettings().getLikedFolder());
        accentColorPicker.setValue(ChoicePlayerApplication.getColors().getAccentColor());
        ObservableList<String> styles = FXCollections
                .observableList(asList(resourceBundle.getString("settingsStyleLight"), resourceBundle.getString("settingsStyleDark")));
        styleComboBox.setItems(styles);
        styleComboBox.getSelectionModel().select(ChoicePlayerApplication.getSettings().getTheme().getStyle().ordinal());

        titleLabel.translateYProperty().bind(titleContainer.heightProperty().subtract(backButton.heightProperty()).divide(2));
        titleLabel.translateXProperty().bind(titleContainer.widthProperty().subtract(titleLabel.widthProperty()).divide(2));
        titleLabel.translateYProperty().bind(titleContainer.heightProperty().subtract(titleLabel.heightProperty()).divide(2));

        setFolderLabelColors();
        folderToLoadLabel.setStackPaneAlignment(CENTER_LEFT);
        folderToMoveLikedMusicLabel.setStackPaneAlignment(CENTER_LEFT);

        backButton.setOnMouseClicked(e -> {
            if (!new File(folderToLoadLabel.getText()).exists() || !new File(folderToMoveLikedMusicLabel.getText()).exists()) {
                rotateCloseButton();
            } else {
                Messenger.send(new SettingsClosedMessage(folderChanged));
            }
        });
    }

    private void rotateCloseButton() {
        RotateTransition rotateTransition = new RotateTransition(Duration.millis(LONG_ANIMATION_DURATION), backButton);
        rotateTransition.setByAngle(360 * 2);
        rotateTransition.setInterpolator(EASE_BOTH);
        rotateTransition.play();
    }

    @FXML
    public void folderToLoadBrowseButtonClicked(MouseEvent mouseEvent) {
        showDirectoryChooser(folderToLoadLabel, value -> {
            ChoicePlayerApplication.getSettings().setFolder(value);
            folderChanged = true;
        });
    }

    @FXML
    public void folderToMoveLikedMusicBrowseButtonClicked(MouseEvent mouseEvent) {
        showDirectoryChooser(folderToMoveLikedMusicLabel, value -> ChoicePlayerApplication.getSettings().setLikedFolder(value));
    }

    private void showDirectoryChooser(AnimatedLabel label, SettingsSetter settingsSetter) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = Paths.get(label.getText()).toFile();
        if (file.exists()) {
            directoryChooser.setInitialDirectory(file);
        }
        File selectedDir = directoryChooser.showDialog(ChoicePlayerApplication.getStage());
        if (selectedDir == null) {
            log.info("No directory selected");
        } else {
            log.info("Directory changed to {}", selectedDir.getAbsolutePath());
            label.setText(selectedDir.getAbsolutePath());
            settingsSetter.set(selectedDir.getAbsolutePath());
            setFolderLabelColors();
        }
    }

    @FXML
    public void accentColorPickerChanged(ActionEvent actionEvent) {
        AccentColor accentColor = ColorConverter.convert(accentColorPicker.getValue());
        if (!ChoicePlayerApplication.getSettings().getTheme().getAccentColor().equals(accentColor)) {
            log.info("Accent color changed to {}", accentColor);
            ChoicePlayerApplication.getSettings().getTheme().setAccentColor(accentColor);
            sendThemeChangedMessage();
        }
    }

    @FXML
    public void styleComboBoxChanged(ActionEvent actionEvent) {
        int selectedIndex = styleComboBox.getSelectionModel().getSelectedIndex();
        ThemeStyle style = ThemeStyle.values()[selectedIndex];
        if (!ChoicePlayerApplication.getSettings().getTheme().getStyle().equals(style)) {
            log.info("Style changed to {}", style);
            ChoicePlayerApplication.getSettings().getTheme().setStyle(style);
            sendThemeChangedMessage();
        }
    }

    private void setFolderLabelColors() {
        setFolderLabelColor(folderToLoadLabel);
        setFolderLabelColor(folderToMoveLikedMusicLabel);
    }

    private void setFolderLabelColor(AnimatedLabel label) {
        if (new File(label.getText()).exists()) {
            label.setTextFill(ChoicePlayerApplication.getColors().getForegroundColor());
        } else {
            label.setTextFill(ChoicePlayerApplication.getColors().getErrorColor());
        }
    }

    private void sendThemeChangedMessage() {
        Messenger.send(new ThemeChangedMessage());
    }

    public void resetFolderChanged() {
        folderChanged = false;
    }

    private interface SettingsSetter {
        void set(String value);
    }
}
