#include "Wire.h"
#include "BMP085.h"
#include "SerialPacket.h"
SerialPacket  sp;
BMP085		  tp;
void setup() {
	Wire.begin();
	sp.begin(115200);
	tp.begin(0x77);
};

void loop() {
	if(sp.isPacketAvailable()) {
		char *cmd = sp.getCommand();
		if(cmd == "TP") {
			char buffer[20];
			int temp, pressure;
			tp.getTP(&temp,&pressure);
			snprintf(buffer,20,"%d %d",temp,pressure);
			sp.sendReply("OK",buffer);
		}
		free(cmd);
	}
};
