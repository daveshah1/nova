#include "E2PROM.h"
E2PROM::E2PROM() {
	current_read_page = 0;
};

void E2PROM::begin_logging() {
	unsigned char firstPage[7];
	bool status = read_page(0, firstPage);
	current_write_page = (firstPage[3] << 8) + firstPage[4];

	//Check if devices are initialised
	if((!status) || (firstPage[0] != 0x4e)) {
		//Try again to be sure
		status == read_page(0, firstPage);
		//Must be blank, format	
		if((!status) || (firstPage[0] != 0x4e)) {
			unsigned char newPage[7] = {0x4e, 0x4f, 0x56,0x00, 0x01, 0x00, 0x00};
			write_page(0, newPage);
                        current_write_page = 1;
		};
	};
	unsigned char page_break[7] = {0x50, 0x41, 0x47, 0x45, 0x00, 0x00, 0x00};
	write_next_page(page_break);
};

unsigned char E2PROM::read_byte(int device, unsigned long address) {
	Wire.beginTransmission((unsigned char)DEVICE_ADDRESS(address >> 16,device));
	Wire.write((address >> 8) & 0xFF); //High byte
	Wire.write(address & 0xFF); //Low byte
	Wire.requestFrom(DEVICE_ADDRESS(address >> 16,device),1);
	Wire.endTransmission();

	if(Wire.available()) {
		return Wire.read();
	} else {
		return 0x00;
	};
};

void E2PROM::write_byte(int device, unsigned long address, unsigned char data) {
	Wire.beginTransmission((unsigned char)DEVICE_ADDRESS(address >> 16,device));
     //   Serial.print("WRITE AT: ");
     //   Serial.println(address,HEX);
	Wire.write((unsigned char)(address >> 8) & 0xFF); //High address byte
	Wire.write((unsigned char)(address & 0xFF)); //Low address byte
	Wire.write(data);
	Wire.endTransmission();
        delay(5);


};


bool E2PROM::read_page(unsigned int page, unsigned char *buffer) {
	int byteCount = 0;	
	unsigned char readPage[8];
	Wire.beginTransmission((unsigned char)DEVICE_ADDRESS((page >> 13) & 0x01,page >> 14));
	Wire.write((page >> 5) & 0xFF); //High address byte
	Wire.write((page * 8) & 0xFF); //Low address byte
	Wire.endTransmission();

	Wire.requestFrom(DEVICE_ADDRESS((page >> 13) & 0x01,page >> 14),8);
	while((Wire.available()) && (byteCount < 8)) {
		readPage[byteCount] = Wire.read();
          //      Serial.print("READ 0x");
//Serial.println(readPage[byteCount],HEX);
		if(byteCount < 7) buffer[byteCount] = readPage[byteCount];
		byteCount++;
	};

	if(byteCount != 8) {
		return false;
	};
	unsigned long tmpsum = 0;
	for(int i = 0;i < 7;i++) {
		tmpsum += (7 - i) * readPage[i];
	};
	unsigned char checksum;
	checksum = tmpsum % 0xFF;
	if(checksum != readPage[7]) {
		return false;
	} else {
		return true;
	};
};	

void E2PROM::write_page(unsigned int page, unsigned char *data) {
	unsigned long tmpsum = 0;
       // Serial.print("WRITE AT PAGE: ");
       // Serial.println(page,HEX);
	for(int i = 0;i < 7;i++) {
		tmpsum += (7 - i) * data[i];
		write_byte(page >> 14, (page & 0x3FFF)*8  + i,data[i]);
	};
	unsigned char checksum;
	checksum = tmpsum % 0xFF;
	write_byte(page >> 14, (page & 0x3FFF)*8 + 7,checksum);
};

void E2PROM::write_next_page(unsigned char *data) {
	if(current_write_page < 32768) {
		write_page(current_write_page,data);
		current_write_page++;
	};
};

void E2PROM::begin_read() {
	current_read_page = 1;
};

bool E2PROM::read_next_page(unsigned char *buffer) {
	bool status = read_page(current_read_page,buffer);
	current_read_page++;
	return status;
};

bool E2PROM::is_end() {
	if(current_read_page >= current_write_page) {
		return true;
	} else {
		return false;
	};
};

unsigned int E2PROM::get_page_to_write() {
	return current_write_page;
};

void E2PROM::update_header() {
	unsigned char newPage[7] = {0x4e, 0x4f, 0x56, 0x00, 0x00, 0x00, 0x00};
	newPage[3] = current_write_page >> 8;
	newPage[4] = current_write_page & 0xFF;
	write_page(0, newPage);
};

void E2PROM::format() {
	unsigned char newPage[7] = {0x4e, 0x4f, 0x56,0x00, 0x01, 0x00, 0x00};
	write_page(0,newPage);
	current_write_page = 1;
	unsigned char page_break[7] = {0x50, 0x41, 0x47, 0x45, 0x00, 0x00, 0x00};
	write_next_page(page_break);
};
