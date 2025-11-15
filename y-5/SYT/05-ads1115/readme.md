# ADS1115

Ansteuerung ADS1115 A/D Wandler mittels I²C. Zu I²C Kommunikation wird die Bibliothek `pigpio` verwendet.

```
#include <stdio.h>
#include <pigpio.h>
#include <unistd.h>

#define I2C_BUS 1
#define ADS1115_ADDR 0x48
#define REG_CONVERSION 0x00
#define REG_CONFIG 0x01

// Funktion zum Byte-Swap (ADS1115 liefert Big-Endian)
int swapBytes(int value) {
    return ((value & 0xFF) << 8) | ((value >> 8) & 0xFF);
}

int main() {
    if (gpioInitialise() < 0) {
        fprintf(stderr, "pigpio Init fehlgeschlagen!\n");
        return 1;
    }

    int handle = i2cOpen(I2C_BUS, ADS1115_ADDR, 0);
    if (handle < 0) {
        fprintf(stderr, "Fehler beim Öffnen des I2C-Geräts!\n");
        gpioTerminate();
        return 1;
    }

    // Config für kontinuierliche Messung auf Kanal A0:
    // - Continuous Mode
    // - Kanal A0
    // - Gain ±4.096V
    // - 128 SPS
    unsigned short config = 0x4183; // MSB zuerst
    i2cWriteWordData(handle, REG_CONFIG, ((config >> 8) & 0xFF) | ((config & 0xFF) << 8));

    while (1) {
        int raw = i2cReadWordData(handle, REG_CONVERSION);
        raw = swapBytes(raw);

        double voltage = (raw * 4.096) / 32768.0; // Umrechnung in Volt
        printf("Messwert: %d, Spannung: %.4f V\n", raw, voltage);

        sleep(1); // 1 Sekunde warten
    }

    i2cClose(handle);
    gpioTerminate();
    return 0;
}
```
## Kompilieren:

`gcc main.c -o main -lpigpio`