#include <LiquidCrystal.h>
#include <DallasTemperature.h>
#include <OneWire.h>

const int rs = 12,
          en = 11, 
          d4 = 6, 
          d5 = 5, 
          d6 = 4, 
          d7 = 3;

const long interval = 500;
           
unsigned long prevMS = 0,
              currMS;

String degC = "Degrees C: ",
       degF = "Degrees F: ";
          
LiquidCrystal lcd(rs, en, d4, d5, d6, d7);
OneWire oneWire(2);
DallasTemperature sensor(&oneWire);

void setup() {
  lcd.begin(16, 2);
  sensor.begin();
  pinMode(7, INPUT);
  Serial.begin(9600);

  getTemps();
  
  degC = "Degrees C: " + String(sensor.getTempCByIndex(0));
  degF = "Degrees F: " + String(sensor.getTempFByIndex(0));
}

void loop() {
  currMS = millis();

  if (currMS - prevMS >= interval) {
    prevMS = currMS;
    
    getTemps();
  
    while (digitalRead(7) == HIGH) {
      
     if (sensor.getTempCByIndex(0) == -127) {
       printToLCD("     Error!     ", "Sensor not found");
         
     } else {
       printToLCD(degC, degF);
     }
       
     getTemps();
    
     if (sensor.getTempCByIndex(0) == -127) {
       printToLCD("     Error!     ", "Sensor not found");
            
     } else {
       degC = "Degrees C: " + String(sensor.getTempCByIndex(0));
       degF = "Degrees F: " + String(sensor.getTempFByIndex(0));
        
       printToLCD(degC, degF);
            
       Serial.print(String(sensor.getTempCByIndex(0)) + "\n");
     }   
    }
    
    Serial.print(String(sensor.getTempCByIndex(0)) + "\n");
    lcd.clear();
    
  }
}

void printToLCD(String str1, String str2) {
  lcd.setCursor(0,0);
  lcd.print(str1);

  lcd.setCursor(0,1);
  lcd.print(str2);
}

void getTemps() {
  sensor.setWaitForConversion(false);
  sensor.requestTemperatures();
  sensor.setWaitForConversion(true);
}
