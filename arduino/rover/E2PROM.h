/*EEPROM definitions
Devices split up into 'blocks' of 8 bytes
Device 0, block 0:
3 bytes start: 4e 4f 56
2 bytes page pointer
2 bytes unused (null for now)
1 byte checksum (see below)
Device 1, block 0:
8 bytes start: 4e 4f 56 41 00 00 00 AE

'Page breaks' separate logging sessions
8 bytes: 50 41 47 45 00 00 00 2D

Data pages: 7 bytes data, 1 byte checksum
Data: 3 bytes pressure (Pa), 2 bytes temperature (C*10), 1 byte Vbat (V*20), 1 byte reserved for future use
Checksum: 7*[Byte 0] + 6*[Byte 1]  + 5*[Byte 2] ... + 1*[Byte 7] modulo 256

Pressure & Vbat unsigned
Temperature signed

Page pointers:
Bits 0 is device ID
remaining bits are page on device
actual address = page id * 8
*/
class E2PROM {
private:
	unsigned int current_write_page;
	unsigned int current_read_page;
	unsigned int end_of_pages;
	void write_byte(int device, unsigned long address, unsigned char data);
	unsigned char read_byte(int device, unsigned long address);
	
public:
	E2PROM(); //Constructor
	bool check_status(); //Check devices are online
	void begin_logging(); //Initialise and insert 'page break'
	void write_next_page(unsigned char *data); //Write 7 bytes of data + checksum
	void write_page(unsigned int page, unsigned char *data); //Write an arbitrary page
	bool read_page(unsigned int page, unsigned char *buffer); //Read an arbitrary page. True=ok, False=error
	void begin_read(); //Reset read pointer
	bool read_next_page(unsigned char *buffer); //Read next page (sequential reads). True=ok, False=error
	bool is_end(); //True if end of EEPROM
	unsigned int current_write_page(); //Get current page to write to
};