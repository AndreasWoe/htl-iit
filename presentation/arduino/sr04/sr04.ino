#include <CAN.h>
#define TRIG_ECHO_PIN 7  // Pin, an den Echo- und Trigger-Pins verbunden sind
 
long duration;
int distance;
 
void setup() {
  while (!Serial);
  // start the CAN bus at 500 kbps 
  if (!CAN. begin(500E3)) {
    Serial.println("Starting CAN failed!"); 
    while (1);
  }

  pinMode(8, OUTPUT); //Buzzer
  pinMode(TRIG_ECHO_PIN, OUTPUT);  // Setzt den Pin als Ausgang für den Trigger-Impuls
  Serial.begin(9600);              // Startet die serielle Kommunikation
}
 
void loop() {
  // Trigger-Impuls senden
  digitalWrite(TRIG_ECHO_PIN, LOW); 
  delayMicroseconds(2); 
  digitalWrite(TRIG_ECHO_PIN, HIGH); 
  delayMicroseconds(10); 
  digitalWrite(TRIG_ECHO_PIN, LOW); 
  // Pin als Eingang setzen, um das Echo-Signal zu empfangen
  pinMode(TRIG_ECHO_PIN, INPUT); 
  duration = pulseIn(TRIG_ECHO_PIN, HIGH); 
  // Distanz in cm berechnen
  distance = duration * 0.034 / 2;

  int category = 0;
 
  if(distance > 70){
    category = 0;
  }
  else if(distance > 50){
    category = 1;
  }
  else if(distance > 40){
    category = 2;
  }
  else if(distance > 25){
    category = 3;
  }
  else if(distance > 10){
    category = 4;
  }
  else{
    category = 5;
  }
  

  // Ergebnis ausgeben
  Serial.print("Entfernung: ");
  Serial.print(distance);
  Serial.println(" cm");
  Serial.print("Kategorie: ");
  Serial.println(category);

  CAN.beginPacket(0x10);
  CAN.write(category);
  CAN.endPacket();

  delay(500);

 
  // Pin wieder als Ausgang setzen, um den nächsten Trigger-Impuls zu senden
  pinMode(TRIG_ECHO_PIN, OUTPUT);
}