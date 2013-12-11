#include "Wire.h"
#include "BMP085.h"
#include "SerialPacket.h"
#include "board.h"
SerialPacket  sp;
MPL3115A2	  tp;

void errorBeep(short data[]) {
	int length = data[0];
	for(int i = 1;i<length;i++) {
		tone(PIEZO,1500);
		if(data[i] == LONG_BEEP) {
			delay(700);
		} else {
			delay(300);
		};
		noTone();
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
	float vBat = ((vBat_raw * Vdd) / 1024.0F) * 2.0F * VBAT_CAL;
	return vBat;
};

//Run Power On Self Tests
void runTests() {
	float vBat = readBattery();
	
};

void setup() {
	Wire.begin();
	sp.begin(9600);
	tp.begin();
	pinMode(STAT_LED,OUTPUT);
	pinMode(LNK_RDY,OUTPUT);

	pinMode(M1A_CTL,OUTPUT);
	pinMode(M1B_CTL,OUTPUT);
	pinMode(M2A_CTL,OUTPUT);
	pinMode(M2B_CTL,OUTPUT);
	
	digitalWrite(STAT_LED,HIGH);
	//Power On Self Test
	runTests();
};

void loop() {
	if(sp.isPacketAvailable()) {
		char cmd[3];
		sp.getCommand(cmd);
		if(strncmp(cmd,"TP",2) == 0) { //Read temperature and pressure
			char buffer[32];
			//Serial.println(temp);
			//Serial.println(pressure);
			snprintf(buffer,32,"%d %ld",(int)tp.readTemp()*10,(long)tp.readPressure());
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
		} else { //Unknown command, return error status.
			sp.sendReply("CE","");
		};
	}
	digitalWrite(STAT_LED,LOW); //Pulse LED as a 'heartbeat' indicator.
	delay(150);
	digitalWrite(STAT_LED,HIGH);
	delay(150);
};
