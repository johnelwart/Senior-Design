package com.example.lab1;
import com.example.lab1.Example;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.round;

public class HelloApplication extends Application {

    final int WINDOW_SIZE = 300;
    public String currUnit = "\u00B0F";
    public boolean isF = true;
    public TempBufferReading buffer = new TempBufferReading();
    public TextField text = new TextField();
    public Label currMinDisplay = new Label();
    public Label currMaxDisplay = new Label();
    public Label currPhoneDisplay = new Label();
    public TextField minUpdate = new TextField();
    public TextField maxUpdate = new TextField();
    public TextField phoneUpdate = new TextField();

    public static final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
    public static final String AUTH_TOKEN = System.getenv("TWILIO_ACCOUNT_AUTH");
    public static final String PHONE = System.getenv("TWILIO_ACCOUNT_PHONE");
    public Example textSender = new Example();
    public boolean textSent = false;
    public Button updateButton = new Button();
    public Button button = new Button();
    BorderPane layout = new BorderPane();
    FlowPane information = new FlowPane(Orientation.VERTICAL);
    final NumberAxis xAxis = new NumberAxis();
    final NumberAxis yAxis = new NumberAxis();
    List<Double> xDataPoints = new ArrayList<Double>();

    public double currMax = 90.0;
    public double currMin = 50.0;

    public String currPhone = "5632759872";
    private ScheduledExecutorService scheduledExecutorService;


    public HelloApplication() throws FileNotFoundException {
    }

    public void sendMessage(String args, String alert) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        // To, From
        Message message = Message.creator(new PhoneNumber("+1" + args),
                new PhoneNumber(PHONE),
                alert).create();

        System.out.println(message.getSid());
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

            for (int x = 0; x < xDataPoints.size(); x++) {
                double val = (5.0 / 9.0) * (xDataPoints.get(x) - 32.0);
                val = val * 100;
                val = Math.round(val);
                val = val / 100;
                xDataPoints.set(x, val);
            }
            double val = (5.0 / 9.0) * (currMin - 32.0);
            val = val * 100;
            val = Math.round(val);
            currMin = val / 100;
            val = (5.0 / 9.0) * (currMax - 32.0);
            val = val * 100;
            val = Math.round(val);
            currMax = val / 100;
            minUpdate.setPromptText("Enter new min value in C");
            maxUpdate.setPromptText("Enter new max value in C");
            currMinDisplay.setFont(Font.font("Verdana", FontWeight.THIN, 12));
            currMinDisplay.setText("Current Min temperature in C:" + currMin);
            currMaxDisplay.setFont(Font.font("Verdana", FontWeight.THIN, 12));
            currMaxDisplay.setText("Current Max temperature in C:" + currMax);
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
            double val = (1.8) * currMin + 32.0;
            val = val * 100;
            val = Math.round(val);
            currMin = val / 100;
            val = (1.8) * currMax + 32.0;
            val = val * 100;
            val = Math.round(val);
            currMax = val / 100;
            minUpdate.setPromptText("Enter new min value in F");
            maxUpdate.setPromptText("Enter new max value in F");
            currMinDisplay.setFont(Font.font("Verdana", FontWeight.THIN, 12));
            currMinDisplay.setText("Current Min temperature in F:" + currMin);
            currMaxDisplay.setFont(Font.font("Verdana", FontWeight.THIN, 12));
            currMaxDisplay.setText("Current Max temperature in F:" + currMax);
        }
    }

    public boolean getisF(){
        return isF;
    }

    public void updateButtonPushed(){
        textSent = false;
        String regex = "[0-9]+";
        String tempString;
        if (!minUpdate.getText().isBlank()){
            tempString = minUpdate.getText();
            if (tempString.matches(regex)){
                currMin = Double.parseDouble(minUpdate.getText());
                if (getisF()){
                    currMinDisplay.setText("Current Max temperature in F:" + currMin);
                }
                else{
                    currMinDisplay.setText("Current Max temperature in C:" + currMin);
                }
            }
        }
        if (!maxUpdate.getText().isBlank()){
            tempString = maxUpdate.getText();
            if (tempString.matches(regex)) {
                currMax = Double.parseDouble(maxUpdate.getText());
                if (getisF()){
                    currMaxDisplay.setText("Current Max temperature in F:" + currMax);
                }
                else{
                    currMaxDisplay.setText("Current Max temperature in C:" + currMax);
                }
            }
        }
        if (!phoneUpdate.getText().isBlank()){
            tempString = phoneUpdate.getText();
            if ((tempString.matches(regex)) && (tempString.length() == 10)){
                currPhone = tempString;
                currPhoneDisplay.setText("Current phone number: " + currPhone);
            }
        }
    }

    public void checkLimits(){
        if ((xDataPoints.get(xDataPoints.size() -1) > currMax) && (!textSent) && (xDataPoints.get(xDataPoints.size() -1) != -200.0)){
            sendMessage(currPhone, "TEMPERATURE HAS EXCEEDED MAX LIMIT");
            textSent = true;
        }
        if ((xDataPoints.get(xDataPoints.size() -1) < currMin) && (!textSent) && (xDataPoints.get(xDataPoints.size() -1) != -200.0)) {
            sendMessage(currPhone, "TEMPERATURE HAS EXCEEDED MIN LIMIT");
            textSent = true;
        }
        if (textSent){
            if ((xDataPoints.get(xDataPoints.size() -1) < currMax) && (xDataPoints.get(xDataPoints.size() -1) > currMin) ) {
                textSent = false;
            }
        }
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
        updateButton.setOnAction(actionEvent -> updateButtonPushed());

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

        updateButton.setText("Update");
        updateButton.setPrefSize(150, 100);

        text.setEditable(false);
        text.setPrefSize(100, 100);
        text.setFont(Font.font("Verdana", FontWeight.BOLD, 40));

        currMinDisplay.setFont(Font.font("Verdana", FontWeight.THIN, 12));
        currMinDisplay.setText("Current Min temperature in F:" + currMin);
        currMaxDisplay.setFont(Font.font("Verdana", FontWeight.THIN, 12));
        currMaxDisplay.setText("Current Max temperature in F:" + currMax);
        currPhoneDisplay.setFont(Font.font("Verdana", FontWeight.THIN, 12));
        currPhoneDisplay.setText("Current phone number: " + currPhone);

        minUpdate.setPromptText("Enter new min value in F");
        maxUpdate.setPromptText("Enter new max value in F");
        phoneUpdate.setPromptText("Enter new phone number");

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
                if (getisF()) {
                    double val = (1.8) * temp_double + 32.0;
                    val = val * 100;
                    val = Math.round(val);
                    temp_double = val / 100;
                }
                int length = series.getData().size();
                series.getData().clear();
                for (int x = 0; x < length; x++){
                    series.getData().add(new XYChart.Data<>( (length-x) * -1, xDataPoints.get(x)));
                }
                if ((tempTemp.contains("-127")) || tempTemp.contains("85")) {
                    text.setText("Current Temperature: NO DATA PROVIDED");
                    series.getData().add(new XYChart.Data<>(0, -200.0));
                    xDataPoints.add(-200.0);
                }
                else{
                    series.getData().add(new XYChart.Data<>(0, temp_double));
                    xDataPoints.add(temp_double);
                    text.setText("Current Temperature: " + xDataPoints.get(xDataPoints.size()-1).toString() + currUnit);
                }
                checkLimits();

            });
        }, 0, 1, TimeUnit.SECONDS);
        information.setVgap(10.0);
        information.getChildren().addAll(button, currMinDisplay, minUpdate, currMaxDisplay, maxUpdate,  currPhoneDisplay, phoneUpdate, updateButton);

        layout.setCenter(lineChart);
        layout.setLeft(information);
        layout.setTop(text);
        Scene scene = new Scene(layout, 1000, 800);

        primaryStage.setScene(scene);

        // show the stage
        primaryStage.show();
    }

}

