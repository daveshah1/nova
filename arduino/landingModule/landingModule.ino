#include <I2Cdev.h>
#include <strings.h>
#include <helper_3dmath.h>
#include <MPU6050.h>

#include <ArduinoStream.h>
#include <bufstream.h>
#include <ios.h>
#include <iostream.h>
#include <istream.h>
#include <MinimumSerial.h>
#include <ostream.h>
#include <Sd2Card.h>
#include <SdBaseFile.h>
#include <SdFat.h>
#include <SdFatConfig.h>
#include <SdFatmainpage.h>
#include <SdFatStructs.h>
#include <SdFatUtil.h>
#include <SdFile.h>
#include <SdInfo.h>
#include <SdSpi.h>
#include <SdStream.h>
#include <SdVolume.h>

#include <Wire.h>

#define ADDRESS 0x77

uint32_t D1 = 0;
uint32_t D2 = 0;

int32_t dT = 0;
int32_t TEMP = 0;
int64_t OFF = 0;
int64_t SENS = 0;
int32_t P = 0;

uint16_t C[7];
const int chipSelect = 4;
SdFat sd;
SdFile myFile;
char filename[14];

MPU6050 accelgyro;

int16_t ax, ay, az;
int16_t gx, gy, gz;

double latitude = 0, longitude = 0, gpsAlt = 0;
int gpshr = 0, gpsmin = 0, gpssec = 0;
void setup() {
    pinMode(28,OUTPUT);  
    Wire.begin();
    Serial.begin(9600);
    Serial1.begin(9600);
    delay(100);
    Serial.println("#Initialising I2C devices");
    initial(ADDRESS);
    accelgyro.initialize();
    Serial.println("#Initialised I2C devices");
    
    Serial.println("#Initialising uSD");
    if (!sd.begin(chipSelect, SPI_HALF_SPEED)) sd.initErrorHalt();
    Serial.println("#Initialised uSD");
    for(int i = 0;i<999;i++) {
      snprintf(filename,14,"log%03d.csv",i);
      if(!sd.exists(filename)) break;
    };
    Serial.print("#Writing to ");
    Serial.println(filename);
    if (!myFile.open(filename, O_RDWR | O_CREAT | O_AT_END)) {
      sd.errorHalt("#Opening file for write failed");
    }
    myFile.println("time,t,p"); 
    myFile.close();
}
long t = 0;
char serialBuffer[500] = {0};
char *bufferPosition = serialBuffer;
long nullPressure = -1;
void loop() {
    if((millis() - t) > 300) {
      digitalWrite(28,HIGH);
      D1 = getVal(ADDRESS, 0x48); // Pressure raw
      D2 = getVal(ADDRESS, 0x58);// Temperature raw
      
      dT = (D2 - C[4] * 256);
      TEMP = 2000 + dT * C[5]/8388608;
      OFF  = C[1] * 65536LL + (dT *C[3])/128;
      SENS = C[0] * 32768LL + (C[2] * dT)/256;
      P = (D1 * SENS/2097152 - OFF)/32768;
      if(nullPressure == -1) nullPressure = P;
      t = millis();
      
      accelgyro.getMotion6(&ax, &ay, &az, &gx, &gy, &gz);
      
      
      Serial.print(t);
      Serial.print(",");
      Serial.print(TEMP);
      Serial.print(",");
      Serial.println(P);
      if (!myFile.open(filename, O_RDWR | O_CREAT | O_AT_END)) {
        sd.errorHalt("#Opening file for write failed");
      }
      myFile.print(t);
      myFile.print(",");
      myFile.print(TEMP);
      myFile.print(",");
      myFile.println(P);
      myFile.close();
     // delay(100);
      digitalWrite(28,LOW);
    //  delay(400);
    }
    while(Serial1.available()) {
      *bufferPosition = Serial1.read();
      if(*bufferPosition == '\n') {
        *(++bufferPosition) = '\0';
        parseNMEA();
        bufferPosition = serialBuffer;
      } else {
        bufferPosition++;
      };
    };
    
};

inline int chrToInt(char c) {
 return c - '0'; 
}

void parseNMEA() {
  char splitstr[18][15];
  int currentPoint = 0;
  char *part;
  //Only parse position updates
  if(strncmp(serialBuffer,"$GPGGA",6)==0) {
    //Disassemble NMEA sentence
    part = strtok(serialBuffer,",");
    while(part != NULL) {
      strncpy(splitStr[currentPoint],part,15);
      part = strtok(NULL,",");
    };
    gpshr = chrToInt(splitstr[1][0]) * 10 + chrToInt(splitStr[1][1]);
    gpsmin = chrToInt(splitstr[1][2]) * 10 + chrToInt(splitStr[1][3]);
    gpssec = chrToInt(splitstr[1][4]) * 10 + chrToInt(splitStr[1][5]);
    int latDegrees = chrToInt(splitstr[2][0]) * 10 + chrToInt(splitStr[2][1]);
    float latMinutes = chrToInt(splitStr[2][2]) * 10.0 + chrToInt(splitStr[2][3]) * 1.0 + chrToInt(splitStr[2][4]) * 0.1 +  chrToInt(splitStr[2][6]) * 0.01 + chrToInt(splitStr[2][7]) * 0.001 +  chrToInt(splitStr[2][8]) * 0.0001;
    latitude = latDegrees + (latMinutes / 60.0);
    
    if(splitStr[3][0]=='S') {
      latitude = -latitude;
    };
    
    int lonDegrees = chrToInt(splitstr[4][0]) * 10 + chrToInt(splitStr[4][1]);
    float lonMinutes = chrToInt(splitStr[4][2]) * 10.0 + chrToInt(splitStr[4][3]) * 1.0 + chrToInt(splitStr[4][4]) * 0.1 +  chrToInt(splitStr[4][6]) * 0.01 + chrToInt(splitStr[4][7]) * 0.001 +  chrToInt(splitStr[4][8]) * 0.0001;
    longitude = lonDegrees + (lonMinutes / 60.0);    
    if(splitStr[5][0]=='W') {
      longitude = -longitude;
    };
  };
};

long getVal(int address, byte code)
{
    unsigned long ret = 0;
    Wire.beginTransmission(address);
    Wire.write(code);
    Wire.endTransmission();
    delay(10);
    // start read sequence
    Wire.beginTransmission(address);
    Wire.write((byte) 0x00);
    Wire.endTransmission();
    Wire.beginTransmission(address);
    Wire.requestFrom(address, (int)3);
    if(Wire.available()) {
    //    ret = ((uint32_t)Wire.read() << 16) | ((uint32_t)Wire.read() << 8) | Wire.read();
            ret = Wire.read() * 65536 + Wire.read() * 256 + Wire.read();
    }
    else {
        ret = -1;
    }
    Wire.endTransmission();
    return ret;
}

void initial(uint8_t address)
{
    Serial.println();
    Serial.println("#PROM COEFFICIENTS");
    
    Wire.beginTransmission(address);
    Wire.write(0x1E); // reset
    Wire.endTransmission();
    delay(10);

    for (int i=0; i<7; i++) {
        Wire.beginTransmission(address);
        Wire.write(0xA2 + (i * 2));
        Wire.endTransmission();

        Wire.beginTransmission(address);
        Wire.requestFrom(address, (uint8_t) 6);
        delay(1);
        if(Wire.available()) {
            C[i] = Wire.read() *256 + Wire.read();
        }
        else {
            Serial.println("#Error reading PROM 1"); // error reading the PROM or communicating with the device
        }
        Serial.println(C[i]);
    }
    Serial.println();
}
