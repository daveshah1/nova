#include "Arduino.h"
#include "SerialPacket.h"

SerialPacket::SerialPacket() {
	packetAvailable = false;
};

/*
Initialises the serial port.
*/
void SerialPacket::begin(int baud) {
	Serial.begin(baud);
}

/*
Run regularly to check for incoming packets.
Returns false if no packet available.
Returns true if a packet was received.
*/
bool SerialPacket::isPacketAvailable() {
	char incomingPacket[64] = {0};
	int currentLocation = 0;
	int startCounter = 0;
	char startBytes[] = {'H','I',' '}; //Set of characters signalling start of transmission
	char cByte;
	while(Serial.available() > 0) {
		cByte = Serial.read();
		//Serial.print("Read byte: ");
		//Serial.println(cByte);
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

/*
Fetches the command part of the received packet.
Buffer must be char array length > 2
*/
void SerialPacket::getCommand(char *buffer) {
	strncpy(buffer,currentPacket,2);//First two bytes are command		
};

/*
Fetches the payload part of the received packet.
Buffer must be char array length > 64
*/
void SerialPacket::getPayload(char *buffer) {
	strncpy(buffer,&(currentPacket[3]),64);//Everything from third byte onwards is the payload		
};

/*
Sends a reply to the master.
status: string containing 2-letter status code
payload: string containing the payload
*/
void SerialPacket::sendReply(char *status,char *payload) {
	Serial.print("RP ");
	Serial.print(status);
	Serial.print(" ");
	Serial.print(payload);
	Serial.println("\n");
};
