#include "Wire.h"
#include "E2PROM.h"
#include "MPL3115A2.h"
#include "SerialPacket.h"
#include "board.h"
SerialPacket  sp;
MPL3115A2     tp;
E2PROM ee;
int lastMillis;
void errorBeep(const short data[]) {
	int length = data[0];
	for(int i = 1;i<length;i++) {
		tone(PIEZO,1500);
		if(data[i] == LONG_BEEP) {
			delay(700);
		} else {
			delay(300);
		};
		noTone(PIEZO);
		delay(250);
	};
};

//Take an average of multiple readings to improve stability and reduce errors.
float readADC_oversample(int pin) {
	long sum = 0;
	for(int i = 0;i < 5;i++) {
		sum += analogRead(pin);
	};
	return (sum / 5.0F);
};

//Return current battery voltage
float readBattery() {
	float vBat_raw = readADC_oversample(VBAT);
	float vBat = ((vBat_raw * VDD) / 1024.0F) * 2.0F * VBAT_CAL;
	return vBat;
};

//Run Power On Self Tests
void runTests() {
	float vBat = readBattery();
	Serial.println(F("# VBAT = "));
        Serial.println(vBat,2);
	if(vBat < VBAT_DEAD) {
		while(1) {
			Serial.println(F("# VBAT is critical!!\n"));
			errorBeep(BEEP_BATTERY_DEAD);
			delay(1000);
		};
	};
	if(vBat < VBAT_WARN) {
		Serial.println(F("# VBAT is low!!\n"));
		errorBeep(BEEP_BATTERY_LOW);
		delay(500);
	};
	
	if(!tp.isPresent()) {
		while(1) {
			Serial.println(F("# MPL3115A2 missing or dead!!\n"));
			errorBeep(BEEP_NO_TP);
			delay(1000);
		};
	};
	
	
};

void setup() {
	Wire.begin();
	sp.begin(9600);
	tp.begin();
        delay(100);
        ee.begin_logging();
	pinMode(STAT_LED,OUTPUT);
	pinMode(LNK_RDY,OUTPUT);

	pinMode(M1A_CTL,OUTPUT);
	pinMode(M1B_CTL,OUTPUT);
	pinMode(M2A_CTL,OUTPUT);
	pinMode(M2B_CTL,OUTPUT);
	
	digitalWrite(STAT_LED,HIGH);
	//Power On Self Test
	runTests();
        lastMillis = millis();
};

void loop() {
        int temp  = tp.readTemp()*10;
        unsigned long pressure = tp.readPressure();
	if(sp.isPacketAvailable()) {
		char cmd[3];
		sp.getCommand(cmd);
		if(strncmp(cmd,"TP",2) == 0) { //Read temperature and pressure
			char buffer[32];
			//Serial.println(temp);
			//Serial.println(pressure);
			snprintf(buffer,32,"%d %ld",temp,pressure);
			sp.sendReply("OK",buffer);
		} else if(strncmp(cmd,"MT",2)==0) { //Motor control
			char params[64];
			sp.getPayload(params);
			bool commandOK = true;
			switch(params[0]) {
			case 'F':
				digitalWrite(M1A_CTL,HIGH);
				digitalWrite(M1B_CTL,LOW);
				break;
			case 'S':
				digitalWrite(M1A_CTL,LOW);
				digitalWrite(M1B_CTL,LOW);
				break;
			case 'B':
				digitalWrite(M1A_CTL,LOW);
				digitalWrite(M1B_CTL,HIGH);
				break;
			default:
				commandOK = false;
				break;
			}
			if(commandOK) {
				switch(params[2]) {
				case 'F':
					digitalWrite(M2A_CTL,HIGH);
					digitalWrite(M2B_CTL,LOW);
					break;
				case 'S':
					digitalWrite(M2A_CTL,LOW);
					digitalWrite(M2B_CTL,LOW);
					break;
				case 'B':
					digitalWrite(M2A_CTL,LOW);
					digitalWrite(M2B_CTL,HIGH);
					break;
				default:
					commandOK = false;
					break;
				}
			}
			if(commandOK) {
				sp.sendReply("OK","");
			} else {
				sp.sendReply("PE","");
			};
                } else if(strncmp(cmd,"RD",2)==0) { //Dump EEPROM
                        ee.begin_read();
                        unsigned  char tmppage[7];
                        while(!ee.is_end()) {
                          ee.read_next_page(tmppage);
                          if((tmppage[0] == 0x50) && (tmppage[1] == 0x41) && (tmppage[2] == 0x47) && (tmppage[3] == 0x45) && (tmppage[4] == 0x00) && (tmppage[5] == 0x00) &&  (tmppage[6] == 0x00)) {
                            Serial.println("--BREAK--");
                          } else {
                            Serial.print((unsigned long)((unsigned long)tmppage[0] * 65536) + (unsigned long)(tmppage[1] * 256) + tmppage[2]);
                            Serial.print(",");
                            Serial.print(((int)tmppage[3] << 8) | (int)tmppage[4]);
                            Serial.print(",");
                            Serial.println(((int)tmppage[5] << 8) | (int)tmppage[6]);
                          };
                        };  
                } else if(strncmp(cmd,"FT",2)==0) { //Format EEPROM (!!!)
                        ee.format();
                        sp.sendReply("OK","");                
		} else if(strncmp(cmd,"PI",2)==0) {
			sp.sendReply("OK","");
		} else { //Unknown command, return error status.
			sp.sendReply("CE","");
		};
	}
        unsigned char eepage[7];
        
        unsigned int deltat = millis() - lastMillis;
        if(deltat > 2000) {
          eepage[0] = (pressure >> 16) & 0xFF;
          eepage[1] = (pressure >> 8) & 0xFF;
          eepage[2] = (pressure) & 0xFF;
          eepage[3] = (temp >> 8) & 0xFF;
          eepage[4] = (temp) & 0xFF;
          eepage[5] = (deltat >> 8) & 0xFF;
          eepage[6] = (deltat) & 0xFF;
          ee.write_next_page(eepage);
          ee.update_header();
          lastMillis = millis();
        };
	digitalWrite(STAT_LED,LOW); //Pulse LED as a 'heartbeat' indicator.
	delay(150);
	digitalWrite(STAT_LED,HIGH);
	delay(150);
};
