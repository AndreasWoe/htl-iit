int pinPWM = 3;     //Power (0 - 255*0.6)
int pinLeft = 4;    //Left or Right
int pinRight = 6;   //Left or Right
 
void setup() {
  pinMode(pinPWM, OUTPUT);
  pinMode(pinRight, OUTPUT);
  pinMode(pinLeft, OUTPUT);
 
}
 
void loop() {
  int y = analogRead(A1); //read one axis from joystick

  digitalWrite(pinLeft, LOW);
  digitalWrite(pinRight, HIGH);
  analogWrite(pinPWM, map(y, 0, 1023, 0, 255));
}