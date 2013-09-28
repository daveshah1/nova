#include "Arduino.h"
#include "SerialPacket.h"

SerialPacket::SerialPacket() {
	packetAvailable = false;
};

void SerialPacket::begin(int baud) {
	Serial.begin(baud);
}

bool SerialPacket::isPacketAvailable() {
	char incomingPacket[64] = {0};
	int currentLocation = 0;
	int startCounter = 0;
	char startBytes[] = {'H','I',' '}; //Set of characters signalling start of transmission
	char cByte;
	while(Serial.available() > 0) {
		cByte = Serial.read();
		Serial.print("Read byte: ");
		Serial.println(cByte);
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
				return true; //If so, finish up and return that a packet was received
			} else  {
				incomingPacket[currentLocation] = cByte;
				currentLocation++;
			}
		};
		delayMicroseconds(2500); //Wait for the next byte, just to prevent errors occuring in some situations.
	};
	return false;
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
	Serial.print("RP ");
	Serial.print(status);
	Serial.print(" ");
	Serial.print(payload);
	Serial.println("\n");
};