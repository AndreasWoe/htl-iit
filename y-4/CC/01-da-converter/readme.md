Folgender Code kann in den Tinkercad Emulator eingefügt werden:

```
const int pins[4] = {8, 9, 10, 11};

void setup()
{
  pinMode(8, OUTPUT);
  pinMode(9, OUTPUT);
  pinMode(10, OUTPUT);
  pinMode(11, OUTPUT);
}

void loop()
{
  int zahl = 8;
  for (int i = 0; i < 4; i++) {
   	int bitWert = bitRead(zahl, i);
    digitalWrite(pins[i], bitWert);
  }
  delay(1000);
}
```