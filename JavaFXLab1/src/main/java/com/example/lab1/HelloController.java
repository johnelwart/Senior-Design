package com.example.lab1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class HelloController {
    @FXML
    private Label fDegrees;

    @FXML
    private Label cDegrees;

    @FXML
    private Label degreeLabel;

    @FXML
    protected void fButtonClick() { degreeLabel.setText("F"); }

    @FXML
    protected void cButtonClick() { degreeLabel.setText("C"); }
}