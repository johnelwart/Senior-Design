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
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class HelloApplication extends Application {
    /*@Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("lab1.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 450);
        //stage.setMaximized(true);
        stage.setTitle("Binary Beasts Lab 1");
        stage.setScene(scene);
        stage.show();
    }*/
    final int WINDOW_SIZE = 300;
    private ScheduledExecutorService scheduledExecutorService;

    public static void main(String[] args) {
        launch();
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

        //defining the axes
        final NumberAxis xAxis = new NumberAxis(); // we are going to plot against time
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Seconds ago from the current time");

        List<Double> xDataPoints =new ArrayList<Double>();

        yAxis.setAutoRanging(false);
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(-300.0);
        xAxis.setUpperBound(0.0);
        xAxis.setAnimated(false); // axis animations are removed
        yAxis.setLabel("Temperature");
        yAxis.setTickUnit(5);
        xAxis.setForceZeroInRange(true);
        yAxis.setLowerBound(55.0);
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
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

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
                Random rand = new Random(); //instance of random class
                int upperbound = 122;
                //generate random values from 0-24
                //int int_random = rand.nextInt(upperbound);
                double random_double = Math.floor(Math.random()*(122-55+1)+55);
                //float float_random=rand.nextFloat();
                //XYChart.Series<Number, Number> tempSeries = new XYChart.Series<>();
                int length = series.getData().size();
                series.getData().clear();

                for (int x = 0; x < length; x++){
                    //System.out.println("here");
                    series.getData().add(new XYChart.Data<>( (length-x) * -1, xDataPoints.get(x)));

                }
                series.getData().add(new XYChart.Data<>(0, random_double));

                xDataPoints.add(random_double);


            });
        }, 0, 1, TimeUnit.SECONDS);



        // setup scene
        Scene scene = new Scene(lineChart, 800, 600);
        primaryStage.setScene(scene);

        // show the stage
        primaryStage.show();
    }

}

