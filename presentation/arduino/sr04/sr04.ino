#define TRIG_ECHO_PIN 7  // Pin, an den Echo- und Trigger-Pins verbunden sind
 
long duration;
int distance;
 
void setup() {
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
 
  // Ergebnis ausgeben
  Serial.print("Entfernung: ");
  Serial.print(distance);
  Serial.println(" cm");
 
  // Pin wieder als Ausgang setzen, um den nächsten Trigger-Impuls zu senden
  pinMode(TRIG_ECHO_PIN, OUTPUT);

  //delay & Buzzer
  digitalWrite(8, HIGH);
  delay(25);
  digitalWrite(8, LOW);
  delay(250);
}