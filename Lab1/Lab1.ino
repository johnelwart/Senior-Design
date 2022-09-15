//www.diyusthad.com
#include <LiquidCrystal.h>
//#include <DS18B20.h> // git repo for library: https://github.com/matmunk/DS18B20/blob/master/README.md
#include <DallasTemperature.h>
#include <OneWire.h>

const int rs = 12, en = 11, d4 = 6, d5 = 5, d6 = 4, d7 = 3;
LiquidCrystal lcd(rs, en, d4, d5, d6, d7);

OneWire oneWire(2);
DallasTemperature sensor(&oneWire);

void setup() {
  lcd.setCursor(1, 0);
  lcd.print("test");
  sensor.begin();
}

void loop() {
  sensor.requestTemperatures();
  //lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print(sensor.getTempCByIndex(0));
  delay(2000);
  
}
