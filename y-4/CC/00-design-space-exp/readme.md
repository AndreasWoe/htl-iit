Folgender Code kann in den Tinkercad Emulator eingefügt werden:

```
void setup()
{
  pinMode(8, OUTPUT); //LED
  pinMode(A0, INPUT); //Drucksensor
  
  //Log Funktion
  Serial.begin(9600);
}

void loop()
{
  int s = analogRead(A0); 	//Sensorwert lesen
  Serial.println(s);		//Ausgabe in Konsole
  
  if(s < 500) {
  	digitalWrite(8, HIGH); //LED einschalten
  }
  else {
  	digitalWrite(8, LOW); //LED ausschalten
  }
}
```