#define SET 16
#define AUX 17
int readline(int readch, char *buffer, int len)
{
  static int pos = 0;
  int rpos;
  
  if (readch > 0) {
    switch (readch) {
      case '\n': // Ignore new-lines
        break;
      case '\r': // Return on CR
        rpos = pos;
        pos = 0;  // Reset position index ready for next time
        return rpos;
      default:
        if (pos < len-1) {
          buffer[pos++] = readch;
          buffer[pos] = 0;
        }
    }
  }
  // No end of line has been found, so return -1.
  return -1;
}
void setup() {
  pinMode(19,INPUT);
  Serial1.begin(9600);
  Serial.begin(57600);
  pinMode(AUX, OUTPUT);
  pinMode(SET, OUTPUT);
  digitalWrite(AUX,LOW);

  digitalWrite(SET,HIGH);
  delay(100);
  digitalWrite(SET,LOW);
  delay(1);
  /*
  434.2 MHz (434200)
  RF 19200 baud (4)
  20mW output power (9)
  UART 9600 baud (3)
  No parity (0)
  */
  Serial1.print("WR_434200_4_9_3_0");
  Serial1.write(0x0D);
  Serial1.write(0X0A);
  delay(200);
  digitalWrite(SET,HIGH);
  delay(200);
};

void loop() {
  if(Serial1.available() > 0) {
    Serial.write(Serial1.read());
  };
};
