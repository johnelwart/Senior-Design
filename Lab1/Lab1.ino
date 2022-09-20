//www.diyusthad.com
#include <LiquidCrystal.h>
//#include <DS18B20.h> // git repo for library: https://github.com/matmunk/DS18B20/blob/master/README.md
#include <DallasTemperature.h>
#include <OneWire.h>

const int rs = 12,
          en = 11, 
          d4 = 6, 
          d5 = 5, 
          d6 = 4, 
          d7 = 3;

const long interval = 2000;
unsigned long prevMS = 0;
          
LiquidCrystal lcd(rs, en, d4, d5, d6, d7);

OneWire oneWire(2);
DallasTemperature sensor(&oneWire);

void setup() {
  lcd.begin(16, 2);
  sensor.begin();
  pinMode(7, INPUT);
}

void loop() {
  if (digitalRead(7) == HIGH){
    unsigned long currMS = millis();

    if (currMS - prevMS >= interval){
      prevMS = currMS;
      sensor.requestTemperatures();
  
      lcd.setCursor(0, 0);
      lcd.print("Degrees C: " + String(sensor.getTempCByIndex(0)));
    
      lcd.setCursor(0,1);
      lcd.print("Degrees F: " + String(sensor.getTempFByIndex(0)));

    }
    
  } else {
    lcd.clear();
  }
  
}
