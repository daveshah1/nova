#include "Arduino.h"
#include "SerialPacket.h"

SerialPacket::SerialPacket(int baud) {
	Serial.begin(baud);
};

int SerialPacket::isPacketAvailable() {
	char[64] incomingPacket = {0};
	int currentLocation = 0;
	int startCounter = 0;
	char[3] startBytes = {'H','I',' '}; //Set of characters signalling start of transmission
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
			strncpy(incomingPacket,currentPacket,64);
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