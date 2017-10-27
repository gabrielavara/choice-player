package com.gabrielavara.musicplayer.controllers;

import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

@FXMLController
public class HelloWorldController {
    @FXML
    private Label helloLabel;

    @FXML
    private TextField nameField;

    // Be aware: This is a Spring bean. So we can do the following:
    // @Autowired
    // private AwesomeActionService actionService;

    // @FXML
    // private void setHelloText(final Event event) {
    // helloLabel.setText(nameField.getText());
    // }
}
