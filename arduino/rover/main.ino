#include "Wire.h"
#include "BMP085.h"
#include "SerialPacket.h"
SerialPacket  sp;
MPL3115A2	  tp;
void setup() {
	Wire.begin();
	sp.begin(9600);
	tp.begin();
	pinMode(A2,OUTPUT);
	pinMode(A1,OUTPUT);

	pinMode(6,OUTPUT);
	pinMode(9,OUTPUT);
	pinMode(10,OUTPUT);
	pinMode(11,OUTPUT);
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
				digitalWrite(6,HIGH);
				digitalWrite(9,LOW);
				break;
			case 'S':
				digitalWrite(6,LOW);
				digitalWrite(9,LOW);
				break;
			case 'B':
				digitalWrite(6,LOW);
				digitalWrite(9,HIGH);
				break;
			default:
				commandOK = false;
				break;
			}
			if(commandOK) {
				switch(params[2]) {
				case 'F':
					digitalWrite(10,HIGH);
					digitalWrite(11,LOW);
					break;
				case 'S':
					digitalWrite(10,LOW);
					digitalWrite(11,LOW);
					break;
				case 'B':
					digitalWrite(10,LOW);
					digitalWrite(11,HIGH);
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
	digitalWrite(A1,LOW); //Pulse LED as a 'heartbeat' indicator.
	digitalWrite(A2,HIGH);
	delay(150);
	digitalWrite(A1,HIGH);
	digitalWrite(A2,LOW);
	delay(150);
};
