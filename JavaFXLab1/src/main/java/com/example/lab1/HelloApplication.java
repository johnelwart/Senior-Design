package com.example.lab1;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.round;

public class HelloApplication extends Application {

    final int WINDOW_SIZE = 300;
    public String currUnit = "\u00B0F";
    public boolean isF = true;

    public TempBufferReading buffer = new TempBufferReading();

    public TextField text = new TextField();
    public Button button = new Button();
    BorderPane layout = new BorderPane();
    //defining the axes
    final NumberAxis xAxis = new NumberAxis(); // we are going to plot against time
    final NumberAxis yAxis = new NumberAxis();
    List<Double> xDataPoints = new ArrayList<Double>();

    public static final DecimalFormat df = new DecimalFormat("0.00");
    private ScheduledExecutorService scheduledExecutorService;

    public HelloApplication() throws FileNotFoundException {
    }

    public static void main(String[] args) {
        launch();
    }

    public void switchUnits(){
        if (isF){
            currUnit = "\u00B0C";
            button.setText("Switch to F");
            yAxis.setLowerBound(10.0);
            yAxis.setUpperBound(50.0);
            yAxis.setLabel("Temperature in " + currUnit);
            isF = false;

            for (int x = 0; x < xDataPoints.size(); x++){
                double val = (5.0/9.0) * (xDataPoints.get(x) - 32.0);
                val = val*100;
                val = Math.round(val);
                val = val /100;
                xDataPoints.set(x, val);
            }
        }
        else{
            currUnit = "\u00B0F";
            button.setText("Switch to C");
            yAxis.setLowerBound(50.0);
            yAxis.setUpperBound(122.0);
            yAxis.setLabel("Temperature in " + currUnit);
            isF = true;

            for (int x = 0; x < xDataPoints.size(); x++){
                double val = (1.8) * xDataPoints.get(x) + 32.0;
                val = val*100;
                val = Math.round(val);
                val = val /100;
                xDataPoints.set(x, val);
            }
        }
    }

    public boolean getisF(){
        return isF;
    }
    @Override
    public void stop() throws Exception{
        super.stop();
        scheduledExecutorService.shutdownNow();
    }

    // Source for displaying live line chart: https://levelup.gitconnected.com/realtime-charts-with-javafx-ed33c46b9c8d
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Binary Beasts Lab 1!");

        xAxis.setLabel("Seconds ago from the current time");

        button.setOnAction(actionEvent -> switchUnits());



        yAxis.setAutoRanging(false);
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(-300.0);
        xAxis.setUpperBound(0.0);
        xAxis.setAnimated(false); // axis animations are removed
        yAxis.setLabel("Temperature in " + currUnit);
        yAxis.setTickUnit(5);
        xAxis.setForceZeroInRange(true);
        yAxis.setLowerBound(50.0);
        yAxis.setUpperBound(122.0);
        yAxis.setAnimated(false); // axis animations are removed

        //creating the line chart with two axis created above
        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Recorded Temperature");
        lineChart.setAnimated(false); // disable animations

        yAxis.setSide(Side.RIGHT);

        //defining a series to display data
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        //series.setName("Data Series");

        // add series to chart
        lineChart.getData().add(series);

        // this is used to display time in HH:mm:ss format

        button.setText("Change to C");
        button.setPrefSize(150, 100);

        text.setEditable(false);
        text.setPrefSize(100, 100);
        text.setFont(Font.font("Verdana", FontWeight.BOLD, 40));
        //text.textProperty().addListener((observable, oldVal, newVal) -> text.setText("Current Temperature: " + xDataPoints.get(0)));

        // setup a scheduled executor to periodically put data into the chart
        ScheduledExecutorService scheduledExecutorService;
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        // put dummy data onto graph per second
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            // get a random integer between 0-10


            // Update the chart
            Platform.runLater(() -> {
                if (series.getData().size() > WINDOW_SIZE)
                {
                    series.getData().remove(0);
                    xDataPoints.remove(0);
                }
                String tempTemp;
                Double temp_double = 22.0;
                try {
                    tempTemp = buffer.getMostRecentData();
                    //System.out.println(tempTemp);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                temp_double = Double.parseDouble(tempTemp);
                if (getisF()){
                    double val = (1.8) * temp_double + 32.0;
                    val = val*100;
                    val = Math.round(val);
                    temp_double = val /100;
                }
                int length = series.getData().size();
                series.getData().clear();

                for (int x = 0; x < length; x++){
                    series.getData().add(new XYChart.Data<>( (length-x) * -1, xDataPoints.get(x)));
                }

                series.getData().add(new XYChart.Data<>(0, temp_double));
                xDataPoints.add(temp_double);
                if (tempTemp == "-127.0"){
                    text.setText("Current Temperature: NO DATA PROVIDED");
                }
                else{
                    text.setText("Current Temperature: " + xDataPoints.get(xDataPoints.size()-1).toString() + currUnit);
                }

            });
        }, 0, 1, TimeUnit.SECONDS);



        layout.setCenter(lineChart);
        layout.setLeft(button);
        layout.setTop(text);
        Scene scene = new Scene(layout, 1000, 800);

        primaryStage.setScene(scene);

        // show the stage
        primaryStage.show();
    }

}

