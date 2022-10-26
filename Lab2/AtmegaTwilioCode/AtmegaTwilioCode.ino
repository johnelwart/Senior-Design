#include <SoftwareSerial.h>

SoftwareSerial BTserial(2, 3);    // Sets up a serial object using pins 2 for the RX and 3 for the TX

char c = ' ';                     // Holds one character of data that is received from the HC-06 on the other circuit
 
void setup() {
    Serial.begin(9600);      // Sets the baud rate of the serial communication to 9600 bits per second
    BTserial.begin(9600);    // Sets the baud rate of the bluetooth communication to 9600 bits per second
}
 
void loop() {
    // If the is data avaiable on the RX pin, read the data one character at a time to c and print it to the serial monitor
    delay(1000);
    Serial.write("1");
}
