#include "Wire.h"
#include "BMP085.h"
#include "SerialPacket.h"
SerialPacket  sp;
BMP085		  tp;
void setup() {
	Wire.begin();
	sp.begin(9600);
	tp.begin();
	pinMode(A2,OUTPUT);
	pinMode(A1,OUTPUT);
};

void loop() {
	if(sp.isPacketAvailable()) {
		char *cmd = sp.getCommand();
		if(strncmp(cmd,"TP",2) == 0) {
			char buffer[32];
			float temp, pressure;
			tp.getTP(temp,pressure);
			//Serial.println(temp);
			//Serial.println(pressure);
			snprintf(buffer,32,"%d %ld",(int)temp*10,(long)pressure);
			sp.sendReply("OK",buffer);
		}
		free(cmd);
	}
	  digitalWrite(A1,LOW);
	  digitalWrite(A2,HIGH);
	  delay(150);
	  digitalWrite(A1,HIGH);
	  digitalWrite(A2,LOW);
	  delay(150);
};
