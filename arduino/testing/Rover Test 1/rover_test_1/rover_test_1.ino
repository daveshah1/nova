//Blink onboard LED and toggle RDY pin
//Ideal as an initial smoketest

void setup() {
  pinMode(A2,OUTPUT);
  pinMode(A1,OUTPUT);
};

void loop() {
  digitalWrite(A1,LOW);
  digitalWrite(A2,HIGH);
  delay(500);
  digitalWrite(A1,HIGH);
  digitalWrite(A2,LOW);
  delay(500);
};
