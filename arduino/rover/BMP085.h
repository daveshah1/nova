#ifndef BMP085_h
#define BMP085_h
#include "Arduino.h"
class BMP085
{
	public:
		BMP085(int address);
        void getTP(int &temperature,int &pressure);	
	private:
		const unsigned char oversampling_setting = 3;
		const unsigned char pressure_waittime[4] = { 5, 8, 14, 26 };
		int I2C_ADDRESS;
		int ac1;
		int ac2;
		int ac3;
		unsigned int ac4;
		unsigned int ac5;
		unsigned int ac6;
		int b1;
		int b2;
		int mb;
		int mc;
		int md;
};
#endif