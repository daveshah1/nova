#include <LSM303.h>
#include <Adafruit_ST7735.h>
#include <Adafruit_GFX.h>
#include <SPI.h>
#include <Wire.h>
#include <strings.h>
#include "lcd.h"

/*--PIN DEFINITIONS--*/
//LCD
#define cs   53
#define dc   46
#define rst  44
//RF
#define SET 16
#define AUX 17
/*-------------------*/

int tol = 2; //Antenna position tolerance

LSM303 compass;
Adafruit_ST7735 tft = Adafruit_ST7735(cs, dc, rst);

float overSampleHeading() {
    float heading = 0;
  for(int i = 0;i<10;i++) {
   compass.read();
   heading += compass.heading();
   delay(1);
  };
 // Serial.println(heading / 10);
  return (heading / 10.0);
};

//Display 'Team Nova' logo with text below
void drawSplash(char *text) {
  tft.fillScreen(ST7735_WHITE);
  
 /// testlines(tft.Color565(255, 0, 255));
  int address = 0;
  for(int i = 0; i < 41; i++) { //41 lines
     for(int j = 0;j<120;j++) { //120 cols
      tft.drawPixel(j+4, i+10,tft.Color565(pgm_read_byte_near(pixel_data+address),
                                           pgm_read_byte_near(pixel_data+address+1),
                                           pgm_read_byte_near(pixel_data+address+2)));
    // tft.drawPixel(j+4,i+10,tft.Color565(255,0,0));
      address += 3;
      //delay(1);   
     };
  };
  tft.setTextColor(ST7735_BLACK);
  //FIXME - center text on screen
  tft.setCursor(23,80);
  tft.setTextSize(2);
  tft.println(text);
};

//For drawing position guidance arrows
void drawArrows(boolean up, boolean down, boolean left, boolean right, uint16_t color) {
  if(up) {
    tft.fillTriangle(48,65,80,65,64,33,color);
  };
  if(down) {
    tft.fillTriangle(48,95,80,95,64,127,color);
  };
  if(left) {
    tft.fillTriangle(48,65,48,95,16,80,color);
  };
   if(right) {
    tft.fillTriangle(80,65,80,95,112,80,color);
  };
};

void setup() {
  pinMode(19,INPUT);
  Serial1.begin(9600);
  Serial.begin(57600);
  pinMode(AUX, OUTPUT);
  pinMode(SET, OUTPUT);
  digitalWrite(AUX,LOW);


  digitalWrite(SET,HIGH);
  Wire.begin();
  compass.init();
  compass.enableDefault();
  //If we have time, change these for maximum accuracy. Probably not critical though.
  compass.m_min = (LSM303::vector<int16_t>){-32767, -32767, -32767};
  compass.m_max = (LSM303::vector<int16_t>){+32767, +32767, +32767};  
  
  tft.initR(INITR_BLACKTAB); 
  tft.fillScreen(ST7735_WHITE);
  delay(100);
  digitalWrite(SET,LOW);
  delay(1);
  /*
  434.2 MHz (434200)
  RF 19200 baud (4)
  20mW output power (9)
  UART 9600 baud (3)
  No parity (0)
  */
  Serial1.print("WR_434200_4_9_3_0");
  Serial1.write(0x0D);
  Serial1.write(0X0A);
  delay(200);
  digitalWrite(SET,HIGH);
  delay(200);
  drawSplash("OFFLINE");
};

char radioBuffer[1500];
char *endOfBuffer = radioBuffer;
char commandBuffer[80];
char *endOfCommandBuffer = commandBuffer;
long timeLastUpdate = 0;

int angleV, angleH;

/*
*0: offline
*1: no data
*2: tracking
*/
int state = 0, lastState = 0;

void loop() {

  //Check for available data on PC interface

  while(Serial.available() > 0) {
    if(state == 0) {
      state = 1;
    };    
    *endOfCommandBuffer = Serial.read();
    //Serial.write(*endOfCommandBuffer);

    if(*(endOfCommandBuffer) == '#') {
      //endOfCommandBuffer++;
      *endOfCommandBuffer = '\0';
      //Handle serial command
      if(strncmp(commandBuffer,"A",1) == 0) { //Set antenna position
        char *part1 = strtok(commandBuffer," ");
        part1 = strtok(NULL," ");
        Serial.println(part1);
        char *part2 = strtok(NULL," ");
        Serial.println(part2);
        angleH = atoi(part1);
        angleV = atoi(part2);
        state = 2;
      }
      else if(strncmp(commandBuffer,"R",1) == 0) { //Read out radio buffer
        for(char *c = radioBuffer;c<endOfBuffer;c++) {
          Serial.write(*c);
        };
        Serial.println();
        Serial.println("-----END-----");
        endOfBuffer = radioBuffer;
      }
      else if(strncmp(commandBuffer,"W",1) == 0) { //Transmit data over radio
        digitalWrite(AUX,HIGH);
        delay(2);
        Serial1.print(commandBuffer+2);
        delay(2);
        digitalWrite(AUX,LOW);
      };
      endOfCommandBuffer = commandBuffer;
    } else {
      endOfCommandBuffer++;
    }
    //Wrap around in event of buffer overflow
    if(endOfCommandBuffer == commandBuffer+80) {
      endOfCommandBuffer = commandBuffer;
    };
  };
    //Check for available data on RF interface
  while(Serial1.available() > 0) {

    *endOfBuffer = Serial1.read();
    endOfBuffer++;
    //Wrap around in event of buffer overflow
    if(endOfBuffer >= radioBuffer+1500) {
      endOfBuffer = radioBuffer;
    };
  };
  if((millis() - timeLastUpdate) > 750) {
    //Update display if appropriate
    timeLastUpdate = millis();

    if((state == 1) && (lastState == 0)) {
      drawSplash("NO DATA");
    };
    if(state == 2) {
      int currentH = (int)overSampleHeading();
      int currentV = 0; //TODO: accelerometer read
      bool up = false, down = false, left = false, right = false;
      tft.fillScreen(ST7735_WHITE);
      if((currentH - angleH) > tol) {
        left = true;
      };
      if((currentH - angleH) < -tol) {
        right = true;
      };
      
      if((currentV - angleV) > tol) {
        down = true;
      };
      if((currentV - angleV) < -tol) {
        up = true;
      };
      drawArrows(up,down,left,right,ST7735_RED);
    };
        lastState = state;
  };
};
