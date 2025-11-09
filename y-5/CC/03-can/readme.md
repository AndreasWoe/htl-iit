# CAN communication Arduino <-> Raspberry Pi

## Using the MCP2515 with Raspberry Pi
The MCP2515 is a popular CAN (Controller Area Network) controller module that allows Raspberry Pi devices to communicate with CAN-enabled systems. This makes it ideal for automotive projects, industrial automation, and IoT applications. This guide explains how to set up and use the MCP2515 with a Raspberry Pi.

### Step 1: Wiring the MCP2515 to the Raspberry Pi
The MCP2515 communicates with the Raspberry Pi using the SPI protocol.

MCP2515 Pin   | Raspberry Pi Pin (5V)
|-------------|----------------------------
| VCC         | 3.3V / 5V (Pin 1)
| GND         | Ground (Pin 6)
| CS          | GPIO8 (Pin 24, SPI0_CE0)
| SO          | GPIO9 (Pin 21, SPI0_MISO)
| SI          | GPIO10 (Pin 19, SPI0_MOSI)
| SCK         | GPIO11 (Pin 23, SPI0_SCLK)
| INT         | GPIO25 (Pin 22)

### Step 2: Enable the SPI Interface on the Raspberry Pi
Use Raspberry Configuration to enable SPI Interface

### Step 3: Install Required Libraries and Tools
```
sudo apt update
sudo apt upgrade -y
sudo apt install -y can-utils
```

### Step 4: Configure the CAN Interface
`sudo nano /boot/firmware/config.txt`
```
# Enable CAN BUS for HTL-Wels boards
# connect INT to GPIO25!
dtoverlay=spi0-1cs,cs0_pin=7
dtoverlay=mcp2515-can0,oscillator=16000000,interrupt=25,spi0-1
dtoverlay=spi-bcm2835
```
`sudo reboot`

`sudo ip link set can0 up type can bitrate 500000`

`ifconfig can0`

### Step 5: Testing the MCP2515
Send a CAN Message: Use the cansend command to send a test message:

`cansend can0 123#DEADBEEF`

Receive a CAN Message: Use the candump command to monitor incoming messages:

`candump can0`

### Step 6: Using Python to Communicate with the MCP2515
#### Installation
`sudo apt install python3-can`
#### Example Python Script
```
import can

# Create a CAN bus instance
bus = can.interface.Bus(channel='can0', bustype='socketcan')

# Send a CAN message
msg = can.Message(arbitration_id=0x123, data=[0xDE, 0xAD, 0xBE, 0xEF], is_extended_id=False)
bus.send(msg)
print("Message sent: ", msg)

# Receive a CAN message
print("Waiting for a message...")
message = bus.recv()
print("Received message: ", message)

```
#### Run
`python3 script.py`

## Using the MCP2515 with Arduino UNO

MCP2515 Pin   | Arduino UNO Pin 
|-------------|----------------------------
| VCC         | VCC 5V
| GND         | Ground
| CS          | 10
| SO          | 12
| SI          | 11
| SCK         | 13
| INT         | 3

## Using the MCP2515 with Arduino Mega 2560

MCP2515 Pin   | Arduino UNO Pin 
|-------------|----------------------------
| VCC         | VCC 5V
| GND         | Ground
| CS          | Pin 10 (default in library, can be changed)
| SO          | Pin 50 (SPI MISO)
| SI          | Pin 51 (SPI MOSI)
| SCK         | Pin 52 (SPI Clock)
| INT         | Pin 2 (Interrupt, optional but recommended)

## Sample Program

Sample Program uses the library:

`https://github.com/sandeepmistry/arduino-CAN`

In Arduino Studio / Libraries serach for: "CAN by Sandeep Mistry"

```
// Copyright (c) Sandeep Mistry. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

#include <CAN.h>

void setup() {
  Serial.begin(9600);
  while (!Serial);

  Serial.println("CAN Sender");

  // start the CAN bus at 500 kbps
  if (!CAN.begin(500E3)) {
    Serial.println("Starting CAN failed!");
    while (1);
  }
}

void loop() {
  // send packet: id is 11 bits, packet can contain up to 8 bytes of data
  Serial.print("Sending packet ... ");

  CAN.beginPacket(0x12);
  CAN.write('h');
  CAN.write('e');
  CAN.write('l');
  CAN.write('l');
  CAN.write('o');
  CAN.endPacket();

  Serial.println("done");

  delay(1000);

  // send extended packet: id is 29 bits,
  // packet can contain up to 8 bytes of data
  Serial.print("Sending extended packet ... ");

  CAN.beginExtendedPacket(0xabcdef);
  CAN.write('w');
  CAN.write('o');
  CAN.write('r');
  CAN.write('l');
  CAN.write('d');
  CAN.endPacket();

  Serial.println("done");

  delay(1000);
}
```