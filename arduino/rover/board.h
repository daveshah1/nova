//Motors
#define M1A_CTL 6
#define M1B_CTL 9
#define M2A_CTL 10
#define M2B_CTL 5

//Analog inputs
#define VBAT A0

//Status monitoring
#define PIEZO 7
#define STAT_LED A2

//Communications
#define LNK_MOSI 0
#define LNK_MISO 1
#define LNK_RDY  A1

#define POD_MISO 4
#define POD_MOSI 8

//Supply voltage
#define VDD 4.8

//Battery measurement calibration
#define VBAT_CAL 1.0

//Beeps
#define LONG_BEEP 1
#define SHORT_BEEP 0

#define BEEP_BATTERY_LOW {4,LONG_BEEP,SHORT_BEEP,SHORT_BEEP}
#define BEEP_BATTERY_LOW {5,LONG_BEEP,SHORT_BEEP,SHORT_BEEP,SHORT_BEEP}

