#include <LiquidCrystal.h>
#include <DallasTemperature.h>
#include <OneWire.h>
#include <SoftwareSerial.h>

const int rs = 12,            // Arduino pin number for the register select pin on the LCD
          en = 11,            // Arduino pin number for the enable pin on the LCD
          d4 = 6,             // Arduino pins for the data lines on the LCD
          d5 = 5, 
          d6 = 4, 
          d7 = 3;

const long interval = 500;    // Half second constant
           
unsigned long prevMS = 0,     // Previous millisecond value that the code executed at
              currMS;         // Stores the current milliseconds since the program began execution using the millis() function

String degC = "Degrees C: ",  // Strings that get printed out to the LCD. At this point the temperatures are not taken yet.
       degF = "Degrees F: ";
          
LiquidCrystal lcd(rs, en, d4, d5, d6, d7);   // Creates an LCD object to control the LCD with the library methods
OneWire oneWire(2);                          // Library used to communicate with one wire temperature sensors
DallasTemperature sensor(&oneWire);          // Creates a temperature sensor object to use the library methods in the code
SoftwareSerial BTSerial(9, 10);              // Creates a serial object to write and read data from the HC-06 bluetooth module using pin 9 for the RX and pin 10 for the TX

void setup() {
  lcd.begin(16, 2);        // Initializes the LCD and sets the dimensions of the display. 16 columns, 2 rows
  sensor.begin();          // Initializes the temperature sensor
  Serial.begin(9600);      // Sets the baud rate or data rate of the serial data transmission to 9600 bits per second
  BTSerial.begin(9600);    // Sets the baud rate of the bluetooth transmission to 9600 bits per second

  pinMode(7, INPUT);       // Configures pin 7 on the Arduino as an input for determining if the button is pressed

  getTemps();              // Calls the helper function to request the temperatures from the DS18B20 asynchronously
  
  degC = "Degrees C: " + String(sensor.getTempCByIndex(0));   // Adds the celsius reading from the sensor to the cooresponding string
  degF = "Degrees F: " + String(sensor.getTempFByIndex(0));   // Adds the fahrenheit reading from the sensor to the cooresponding string
}

void loop() {

  currMS = millis();     // Gets the number of milliseconds that have passed since the program began and stores it in currMS

  // If the number of milliseconds since the last execution of the statement is greater or equal to 500 then execute the code again
  if (currMS - prevMS >= interval) {
    prevMS = currMS;     // Store the current time as the new previous execution
    
    getTemps();          // Get the temperatures from the sensor asynchronously to increase speed and responsiveness

    // Check if the button is pushed
    while (digitalRead(7) == HIGH) {

     // Check for an error code from the sensor
     if (sensor.getTempCByIndex(0) == -127) {
       printToLCD("     Error!     ", "Sensor not found");    // Print an error message to the LCD using the helper function
         
     } else {                     // Otherwise, print the previous data from the last time the LCD was active
       printToLCD(degC, degF);
     }
       
     getTemps();                  // Get the temperatures again to make sure they are accurate and up to date

     // Check for an error code from the sensor
     if (sensor.getTempCByIndex(0) == -127) {
       printToLCD("     Error!     ", "Sensor not found");          // Print the error message to the LCD
            
     } else {
       degC = "Degrees C: " + String(sensor.getTempCByIndex(0));    // Generate a new celsius string to display to the LCD
       degF = "Degrees F: " + String(sensor.getTempFByIndex(0));    // Generate a new fahrenheit string to display
        
       printToLCD(degC, degF);    // Print the new strings to the LCD using the helper function

       BTSerial.print(String(sensor.getTempCByIndex(0)) + "\n");    // Send the sensor data to the bluetooth module for transmission to the HC-05
     }   
    }
    
    BTSerial.print(String(sensor.getTempCByIndex(0)) + "\n");    // Sends the sensor data to the bluetooth module for transmission
    lcd.clear();    // Clears the LCD
    
  }
}

// Helper function to simplify the code. This function takes two strings for each line on the LCD and prints them accordingly using setCursor
void printToLCD(String str1, String str2) {
  lcd.setCursor(0,0);
  lcd.print(str1);

  lcd.setCursor(0,1);
  lcd.print(str2);
}

// Helper function that requests the temperatures from the sensors asynchronously and simplifies the code.
void getTemps() {
  sensor.setWaitForConversion(false);
  sensor.requestTemperatures();
  sensor.setWaitForConversion(true);
}
