package com.example.lab1;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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
    private LineChart lineChart;

    @FXML
    protected void fButtonClick() { degreeLabel.setText("F"); }
    @FXML
    protected void cButtonClick() { degreeLabel.setText("C"); }

}