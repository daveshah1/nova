#include "Wire.h"
#include "Arduino.h"

#ifndef BMP085_h
#define BMP085_h

class BMP085
{
	public:
		void begin(unsigned char address);
        void getTP(int *temperature,int *pressure);	
	private:
		void getCalData();
		unsigned int readUT();
		long readUP();
		void write_register(unsigned char r, unsigned char v);
		char read_register(unsigned char r);
		int read_int_register(unsigned char r);
		const unsigned char oversampling_setting = 3;
		const unsigned char pressure_waittime[4] = { 5, 8, 14, 26 };
		unsigned char I2C_ADDRESS;
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
