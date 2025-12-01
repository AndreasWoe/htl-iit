// Copyright (c) Sandeep Mistry. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

#include <CAN.h>
#define BUZZER_PIN 7

int lastDistance=0;
unsigned long lastBeep;

void setup() {
  Serial.begin(9600);
  pinMode(BUZZER_PIN, OUTPUT);
  while (!Serial);

  Serial.println("CAN Receiver");

  // start the CAN bus at 500 kbps
  if (!CAN.begin(500E3)) {
    Serial.println("Starting CAN failed!");
    while (1);
  }

  lastBeep = millis();
}

void loop() {
  // try to parse packet
  int packetSize = CAN.parsePacket();

  if (packetSize || CAN.packetId() != -1) {
    // received a packet
    Serial.print("Received ");

    if (CAN.packetExtended()) {
      Serial.print("extended ");
    }

    if (CAN.packetRtr()) {
      // Remote transmission request, packet contains no data
      Serial.print("RTR ");
    }

    Serial.print("packet with id 0x");
    Serial.print(CAN.packetId(), HEX);

    if (CAN.packetRtr()) {
      Serial.print(" and requested length ");
      Serial.println(CAN.packetDlc());
    } else {
      Serial.print(" and length ");
      Serial.println(packetSize);

      // only print packet data for non-RTR packets
      while (CAN.available()) {
        lastDistance = CAN.read();
        Serial.print(lastDistance);
      }
      Serial.println();
    }

    Serial.println();
  }
  
  if (lastDistance >= 2 && lastDistance <= 5) {

      unsigned long interval = 0;

      switch (lastDistance) {
        case 2: interval = 700; break;  
        case 3: interval = 300; break;  
        case 4: interval = 100; break;  
        case 5: interval = 0; break;
      }

      if (millis() - lastBeep >= interval) {
        lastBeep = millis();
        digitalWrite(BUZZER_PIN, HIGH);
        delay(20);                 
        digitalWrite(BUZZER_PIN, LOW);
      }

  } else {
      digitalWrite(BUZZER_PIN, LOW); //Nicht piepsen
  }

}

