#ifndef SerialPacket_h
#define SerialPacket_h
#include "stdlib.h"
#include "string.h"
#include "Arduino.h"
class SerialPacket
{
public:
	SerialPacket();
	bool isPacketAvailable();
	void getCommand(char *buffer);
	void getPayload(char *buffer);
	void sendReply(char *status,char *payload);
	void begin(int baud);
private:
	char currentPacket[64];
	bool packetAvailable;
};
#endif