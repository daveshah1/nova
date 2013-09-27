#ifndef SerialPacket_h
#define SerialPacket_h
#include "stdlib.h"
#include "string.h"
#include "Arduino.h"
class SerialPacket
{
	public:
		SerialPacket(int baud);
		int isPacketAvailable();
		char *getCommand();
		char *getPayload();
		void sendReply(char *status,char *payload);		
	private:
		char currentPacket[64];
		bool packetAvailable;
};
#endif