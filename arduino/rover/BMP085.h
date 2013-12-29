#include "Wire.h"
#include "Arduino.h"

#ifndef BMP085_h
#define BMP085_h
#define BMP085_ADDRESS 0x77  // I2C address of BMP085

class BMP085
{
	public:
		void begin();
        void getTP(float &t, float &p);
        float calcAltitude(float pressure);
	private:
        const static unsigned char OSS = 0;  // Oversampling Setting
        void bmp085Calibration();
        float bmp085GetTemperature(unsigned int ut);
        long bmp085GetPressure(unsigned long up);
        char bmp085Read(unsigned char address);
        int bmp085ReadInt(unsigned char address);
        unsigned int bmp085ReadUT();
        unsigned long bmp085ReadUP();
        void writeRegister(int deviceAddress, byte address, byte val);
        int readRegister(int deviceAddress, byte address);
        
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
        long b5; 
};
#endif
