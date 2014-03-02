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
bool gpsAvailable = false;
double latitude = 0, longitude = 0, gpsAlt = 0;
int gpshr = 0, gpsmin = 0, gpssec = 0;

enum deployment_state {
  WAITING = 0,
  LAUNCHING = 1,
  LAUNCHED = 2,
  LANDING = 3,
  LANDED = 4,
  READY_TO_DEPLOY = 5,
  DEPLOYING = 6,
  DEPLOYED = 7,
  CANCELLED = 8
};

deployment_state currentState = WAITING;

#define AUX 20
#define SET 27

#define M2A_CTL 15
#define M2B_CTL 14

#define MS_OPEN 24

bool useSD = false;

void openLatch() {
  long startTime = millis();
  digitalWrite(M2A_CTL,HIGH);
  digitalWrite(M2B_CTL,LOW);
  while((digitalRead(MS_OPEN) == 1) && ((millis() - startTime) > 7500));
  digitalWrite(M2A_CTL,LOW);
  digitalWrite(M2B_CTL,LOW);    
};


void setup() {
    currentState = WAITING;
    pinMode(MS_OPEN,INPUT); //NB: 1 = switch inactive, 0 = switch active
    pinMode(M2A_CTL,OUTPUT);
    pinMode(M2B_CTL,OUTPUT);
    pinMode(28,OUTPUT);  
    pinMode(AUX, OUTPUT);
    pinMode(SET, OUTPUT);
    Wire.begin();
    Serial.begin(9600);
    Serial1.begin(9600);
    digitalWrite(SET,HIGH);
    delay(100);
    digitalWrite(SET,LOW);
    /*
    434.2 MHz (434200)
    RF 19200 baud (4)
    20mW output power (9)
    UART 9600 baud (3)
    No parity (0)
    */
    delay(2);
    Serial.println("WR_434200_4_9_3_0");
    delay(200);
    digitalWrite(SET,HIGH);
    delay(200);
    Serial.println("#Initialising I2C devices");
    initial(ADDRESS);
    accelgyro.initialize();
    Serial.println("#Initialised I2C devices");
    
    Serial.println("#Initialising uSD");
    if (!sd.begin(chipSelect, SPI_SIXTEENTH_SPEED)) {
      sd.initErrorHalt();
         useSD = false;
        Serial.println("---SD ERROR---");
    } else {
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
      myFile.println("time,t,p,gps,lat,lon,alt,ax,ay,az,gx,gy,gz"); 
      myFile.close();
    }
}
long t = 0;
char serialBuffer[500] = {0};
char *bufferPosition = serialBuffer;
long nullPressure = -1;

float altitudeStateStore = 0;
long timeStateStore = 0;
char ch1 = 0, ch2 = 0, ch3 = 0;
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
      
      digitalWrite(AUX,HIGH);
      delay(20);
      Serial.print("START");
      Serial.print(TEMP);
      Serial.print(",");
      Serial.print(P);
      Serial.print(",");
      Serial.print(gpsAvailable);
      Serial.print(",");
      Serial.print(latitude,8);
      Serial.print(",");
      Serial.print(longitude,8);
      Serial.print(",");
      Serial.print(gpsAlt);
      Serial.print(",");
      Serial.print(currentState);
      Serial.print(",");
      Serial.print(millis() - timeStateStore);
      Serial.print(",");
      
      Serial.print(relativisePressure(P));
      Serial.println("END");
      delay(10);
      digitalWrite(AUX,LOW);
     // digitalWrite(AUX,LOW);
      if (!myFile.open(filename, O_RDWR | O_CREAT | O_AT_END)) {
        //sd.errorHalt("#Opening file for write failed");
        useSD = false;
        Serial.println("---SD ERROR---");
      } else {
        myFile.print(t);
        myFile.print(",");
        myFile.print(TEMP);
        myFile.print(",");
        myFile.print(P);
        myFile.print(",");
        myFile.print(gpsAvailable);
        myFile.print(",");
        myFile.print(latitude,8);
        myFile.print(",");
        myFile.print(longitude,8);
        myFile.print(",");
        myFile.print(gpsAlt);
        myFile.print(",");
        myFile.print(ax);      
        myFile.print(",");
        myFile.print(ay);   
        myFile.print(",");
        myFile.print(az);
        myFile.print(",");
        myFile.print(gx);      
        myFile.print(",");
        myFile.print(gy);   
        myFile.print(",");
        myFile.println(gz);      
        myFile.close();
      };
     // delay(100);
      digitalWrite(28,LOW);
    //  delay(400);
    }
    while(Serial1.available()) {
      *bufferPosition = Serial1.read();
      if(*bufferPosition == '\n') {
        *(++bufferPosition) = '\0';
        Serial.println(serialBuffer);
        Serial.print("END");
        parseNMEA();
        bufferPosition = serialBuffer;
      } else {
        bufferPosition++;
        if(bufferPosition >= (serialBuffer+500)) {
          bufferPosition = serialBuffer; 
        }
      };
    };
    switch(currentState) {
     case WAITING:
        if(relativisePressure(P) > 50) {
          currentState = LAUNCHING;
          timeStateStore = millis();
          altitudeStateStore = relativisePressure(P);
        };
        break;
     case LAUNCHING:
        if((millis() - timeStateStore) > 10000) {
          if(abs(altitudeStateStore - relativisePressure(P)) < 5) {
            currentState = LAUNCHED;
          };
          if((altitudeStateStore - relativisePressure(P)) > 25) {
            currentState = LANDING;
          };
          altitudeStateStore = relativisePressure(P);
          timeStateStore = millis();
        };
        break;
     case LAUNCHED:
        if((altitudeStateStore - relativisePressure(P)) > 50) {
           currentState = LANDING;
           altitudeStateStore = relativisePressure(P);
           timeStateStore = millis();
        };
        break;
      case LANDING:
        if((millis() - timeStateStore) > 30000) {
          if(abs(altitudeStateStore - relativisePressure(P)) < 5) {
            currentState = LANDED;
          };
          altitudeStateStore = relativisePressure(P);
          timeStateStore = millis();
        };
        break;
      case LANDED:
        if((millis() - timeStateStore) > 30000) {
          currentState = READY_TO_DEPLOY;
        };
        break;
      case READY_TO_DEPLOY:
        if((millis() - timeStateStore) > 90000) {
          currentState = DEPLOYING;
        };
        break;
      case DEPLOYING:
        //Motor and microswitch stuff
        openLatch();
        currentState = DEPLOYED;
        break;
      case DEPLOYED:
        //Nothing here for now
        break;
    }
    while(Serial.available() > 0) {
      ch1 = ch2;
      ch2 = ch3;
      ch3 = Serial.read();
      if((ch1 == 'E') && (ch2 == 'N') && (ch3 == 'D')) {
        currentState = CANCELLED;
      };
      if((ch1 == 'D') && (ch2 == 'E') && (ch3 == 'P')) {
        currentState = DEPLOYING;
      };
    };
};

inline int chrToInt(char c) {
 return c - '0'; 
}
//Convert pressure to relative altitude
float relativisePressure(float Pr) {
  float altitude;
  if(nullPressure == -1) return 0;
  altitude = 44330.0 * (1.0 - pow(Pr / nullPressure,0.1903));
  return altitude;
}

void parseNMEA() {
  char splitstr[18][15];
  int currentPoint = 0;
  //Only parse position updates
  if(strncmp(serialBuffer,"$GPGGA",6)==0) {
    //Disassemble NMEA sentence
    int index = 0, index2 = 0;
    char currentChar  = serialBuffer[0];
    while((currentChar != '\0') && (currentPoint < 18) && (index < 500)) {
      if((currentChar == ',') || (index2 > 14)) {
        splitstr[currentPoint][index2] = '\0';
        currentPoint++;
        index2 = 0;
      } else {
        splitstr[currentPoint][index2] = currentChar;
        index2++;
      };
      index++;
      currentChar = serialBuffer[index];
    };
    if(currentPoint < 13) {
      Serial.println("Rejecting bad GPGGA");
      return;
    };
    gpshr = chrToInt(splitstr[1][0]) * 10 + chrToInt(splitstr[1][1]);
    gpsmin = chrToInt(splitstr[1][2]) * 10 + chrToInt(splitstr[1][3]);
    gpssec = chrToInt(splitstr[1][4]) * 10 + chrToInt(splitstr[1][5]);
    int latDegrees = chrToInt(splitstr[2][0]) * 10 + chrToInt(splitstr[2][1]);
    float latMinutes = chrToInt(splitstr[2][2]) * 10.0 + chrToInt(splitstr[2][3]) * 1.0 + chrToInt(splitstr[2][5]) * 0.1 +  chrToInt(splitstr[2][6]) * 0.01 + chrToInt(splitstr[2][7]) * 0.001 +  chrToInt(splitstr[2][8]) * 0.0001;
    latitude = latDegrees + (latMinutes / 60.0);
    
    if(splitstr[3][0]=='S') {
      latitude = -latitude;
    };
    
    int lonDegrees = chrToInt(splitstr[4][0]) * 100 + chrToInt(splitstr[4][1]) * 10 + chrToInt(splitstr[4][2]);
    float lonMinutes = chrToInt(splitstr[4][3]) * 10.0 + chrToInt(splitstr[4][4]) * 1.0 + chrToInt(splitstr[4][6]) * 0.1 +  chrToInt(splitstr[4][7]) * 0.01 + chrToInt(splitstr[4][8]) * 0.001 +  chrToInt(splitstr[4][9]) * 0.0001;
    longitude = lonDegrees + (lonMinutes / 60.0);    
    if(splitstr[5][0]=='W') {
      longitude = -longitude;
    };
    
    gpsAlt = atof(splitstr[9]);
    Serial.println(splitstr[7]);
    if(chrToInt(splitstr[6][0])>0) {
     gpsAvailable = true;
    } else {
     gpsAvailable = false;
    }
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
