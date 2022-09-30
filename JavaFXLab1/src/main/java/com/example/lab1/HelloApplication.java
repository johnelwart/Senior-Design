package com.example.lab1;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message; // Text message functionality
import com.twilio.type.PhoneNumber;
import javafx.application.Application;
import javafx.application.Platform; // Necessary libraries to interact and display data to the graph
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
import javafx.stage.Stage; // Library for creating the stage for all components to go on to
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors; // Libraries needed for multi threading
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HelloApplication extends Application {
    final int WINDOW_SIZE = 300; // Setting how many data points we want to keep track of
    public String currUnit = "\u00B0F"; // Default unit is F, ASCII code for the degree symbol
    public boolean isF = true; // Boolean to remind us what the current unit is
    public TempBufferReading buffer = new TempBufferReading(); // Buffer object to read data from a file
    public TextField text = new TextField();
    public Label currMinDisplay = new Label(); // All JavaFX components
    public Label currMaxDisplay = new Label();
    public Label currPhoneDisplay = new Label();
    public TextField minUpdate = new TextField();
    public TextField maxUpdate = new TextField();
    public TextField phoneUpdate = new TextField();
    public static final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID"); // Twilio verification setup
    public static final String AUTH_TOKEN = System.getenv("TWILIO_ACCOUNT_AUTH");
    public static final String PHONE = System.getenv("TWILIO_ACCOUNT_PHONE");
    public boolean textSent = false;
    public Button updateButton = new Button();
    public Button button = new Button();
    BorderPane layout = new BorderPane();
    FlowPane information = new FlowPane(Orientation.VERTICAL); // Organizing components
    final NumberAxis xAxis = new NumberAxis();
    final NumberAxis yAxis = new NumberAxis();
    List<Double> xDataPoints = new ArrayList<Double>();
    public double currMax = 90.0; // Default temperature values and phone number
    public double currMin = 50.0;
    public String currPhone = "5632759872";
    private ScheduledExecutorService scheduledExecutorService;
    public HelloApplication() throws FileNotFoundException {
    }

    public void sendMessage(String args, String alert) { // Function to call other class function to send personalized message
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        // To, From
        Message message = Message.creator(new PhoneNumber("+1" + args),
                new PhoneNumber(PHONE),
                alert).create();

        System.out.println(message.getSid());
    }

    public static void main(String[] args) {
        launch();
    } // Initializes GUI for JavaFX

    public void switchUnits(){ // Switching between C and F units
        if (isF){ // Code below is to switch everything from F to C
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
        else{ // Code below is to switch from C to F
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
    } // Getter for the boolean that tells us what the current unit is

    public void updateButtonPushed(){
        textSent = false;
        String regex = "[0-9]+"; // Setting regex to not allow certain inputs from user
        String tempString;
        if (!minUpdate.getText().isBlank()){ // Deals with if input is in the minimum temperature update
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
        if (!maxUpdate.getText().isBlank()){ // Deals with if there is an input in the maximum temperature update
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
        if (!phoneUpdate.getText().isBlank()){ // Deals with if there is a new number to send text messages
            tempString = phoneUpdate.getText();
            if ((tempString.matches(regex)) && (tempString.length() == 10)){
                currPhone = tempString;
                currPhoneDisplay.setText("Current phone number: " + currPhone);
            }
        }
    }

    public void checkLimits(){ // Function used to tell when to send a text message
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
    public void stop() throws Exception{ // Stops our multithreading from running when application is closed
        super.stop();
        scheduledExecutorService.shutdownNow();
    }

    // Source for displaying live line chart: https://levelup.gitconnected.com/realtime-charts-with-javafx-ed33c46b9c8d
    @Override
    public void start(Stage primaryStage) throws Exception { // Ran from the launch method, creates graph and window

        primaryStage.setTitle("Binary Beasts Lab 1!");
        xAxis.setLabel("Seconds ago from the current time");
        button.setOnAction(actionEvent -> switchUnits());
        updateButton.setOnAction(actionEvent -> updateButtonPushed());

        yAxis.setAutoRanging(false); // Configuring axis information
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
        // add series to chart
        lineChart.getData().add(series);
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
        scheduledExecutorService.scheduleAtFixedRate(() -> { // Creating thread to independently run every second
            Platform.runLater(() -> {
                if (series.getData().size() > WINDOW_SIZE) // Removes data if more than 300 data points
                {
                    series.getData().remove(0);
                    xDataPoints.remove(0);
                }
                String tempTemp;
                Double temp_double = 22.0;
                try {
                    tempTemp = buffer.getMostRecentData(); // Getting most recent data as a string
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                temp_double = Double.parseDouble(tempTemp);
                if (getisF()) { // Formatting to a double with two decimal places
                    double val = (1.8) * temp_double + 32.0;
                    val = val * 100;
                    val = Math.round(val);
                    temp_double = val / 100;
                }
                int length = series.getData().size();
                series.getData().clear();
                for (int x = 0; x < length; x++){ // Updating all data points to shift to the left
                    series.getData().add(new XYChart.Data<>( (length-x) * -1, xDataPoints.get(x)));
                }
                if ((tempTemp.contains("-127")) || tempTemp.contains("85")) { // Setting custom message for when temperature sensor throws error
                    text.setText("Current Temperature: NO DATA PROVIDED");
                    series.getData().add(new XYChart.Data<>(0, -200.0));
                    xDataPoints.add(-200.0);
                }
                else{
                    series.getData().add(new XYChart.Data<>(0, temp_double));
                    xDataPoints.add(temp_double);
                    text.setText("Current Temperature: " + xDataPoints.get(xDataPoints.size()-1).toString() + currUnit);
                }
                checkLimits(); // Checks if we need to text every second

            });
        }, 0, 1, TimeUnit.SECONDS); // Runs thread every one second
        information.setVgap(10.0);
        information.getChildren().addAll(button, currMinDisplay, minUpdate, currMaxDisplay, maxUpdate,  currPhoneDisplay, phoneUpdate, updateButton);
        layout.setCenter(lineChart); // Adding all components to window with configurations
        layout.setLeft(information);
        layout.setTop(text);
        Scene scene = new Scene(layout, 1000, 800);
        primaryStage.setScene(scene);
        // show the stage
        primaryStage.show();
    }

}

