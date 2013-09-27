#include "Arduino.h"
#include "SerialPacket.h"

SerialPacket::SerialPacket(int baud) {
	packetAvailable = false;
	Serial.begin(baud);
	
};

int SerialPacket::isPacketAvailable() {
	char incomingPacket[64] = {0};
	int currentLocation = 0;
	int startCounter = 0;
	char startBytes[] = {'H','I',' '}; //Set of characters signalling start of transmission
	char cByte;
	while(Serial.available() > 0) {
		cByte = Serial.read();
		if (startCounter < 3) {
			if (cByte == startBytes[startCounter]) {
				startCounter++;
			} else {
				startCounter = 0;
			};
		} else {
		if (cByte == '\n') { //Check for terminating character
			packetAvailable = true;
			strncpy(currentPacket,incomingPacket,64);
			return 1; //If so, finish up and return that a packet was received
		} else  {
			incomingPacket[currentLocation] = cByte;
			currentLocation++;
		}
		};
		delayMicroseconds(2500); //Wait for the next byte, just to prevent errors occuring in some situations.
	};
	return 0;
};

char *SerialPacket::getCommand() {
	if(packetAvailable) {
		char *cmd = (char*)malloc(2);
		strncpy(cmd,currentPacket,2);//First two bytes are command		
		return cmd;
	} else {
		return NULL;
	};
};

char *SerialPacket::getPayload() {
	if(packetAvailable) {
		char *payload = (char*)malloc(64);
		strncpy(payload,&(currentPacket[3]),64);//Everything from third byte onwards is the payload		
		return payload;
	} else {
		return NULL;
	};
};

void SerialPacket::sendReply(char *status,char *payload) {
	char replyPacket[65];
	snprintf(replyPacket,64,"RP %s %s \n",status,payload);
	replyPacket[64] = 0x00; //Just in case, ensure a valid terminator is set.
	Serial.write(replyPacket);
};
