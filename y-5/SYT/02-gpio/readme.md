# GP I/Os am Raspberry Pi 2B nutzen

## pigpiod-Daemon starten
`sudo pigpiod`

## Dokumentation der Library

https://abyz.me.uk/rpi/pigpio/cif.html

## Beispielprogramm kompilieren

Dateiname: main.c

```
#include <stdio.h>
#include <pigpio.h>
#include <unistd.h>  //sleep

#define GPIO_PIN 17  //GPIO-Pin-Nummer (BCM-Nummerierung)

int main() {
    if (gpioInitialise() < 0) {
        fprintf(stderr, "pigpio konnte nicht initialisiert werden.\n");
        return 1;
    }

    gpioSetMode(GPIO_PIN, PI_OUTPUT);

    while (1) {
        gpioWrite(GPIO_PIN, 1);  // HIGH
        gpioSleep(PI_TIME_RELATIVE, 1, 0);  // 1 Sekunde
        gpioWrite(GPIO_PIN, 0);  // LOW
        gpioSleep(PI_TIME_RELATIVE, 1, 0);  // 1 Sekunde
    }

    gpioTerminate();
    return 0;
}
```

`gcc -o main main.c -lpigpio -lrt -lpthread`

Falls Geany als IDE verwendet wird, können bzw. müssen die Parameter `-lpigpio -lrt -lpthread` in Build / Set Build Commands / C Commands / Build hinzugefügt werden.

### -lpigpio
- Bedeutung: Linkt dein Programm mit der pigpio-Bibliothek.
- Funktion: Diese Bibliothek stellt Funktionen zur Steuerung der GPIO-Pins des Raspberry Pi bereit, z. B. gpioWrite(), gpioRead(), gpioSetMode() usw.
- Pfad: Normalerweise liegt die Bibliothek unter /usr/lib/libpigpio.so.
### -lrt
- Bedeutung: Linkt mit der Real-Time Library (librt).
- Funktion: Wird benötigt für Funktionen wie clock_nanosleep() oder timer_create(), die in pigpio intern verwendet werden, um präzise Zeitsteuerung zu ermöglichen.
- Hinweis: Auf neueren Systemen ist librt oft in die Standard-C-Bibliothek integriert, aber pigpio verlangt sie explizit.
### -lpthread
- Bedeutung: Linkt mit der POSIX-Thread-Bibliothek.
- Funktion: pigpio verwendet Threads, z. B. für parallele Verarbeitung von GPIO-Ereignissen oder Hintergrunddienste. Diese Bibliothek stellt Funktionen wie pthread_create() oder pthread_join() bereit.

## Beispielprogramm ausführen

Ausführen: `sudo ./main`

Falls der pigpiod-Daemon bereits läuft und blockiert:

`sudo killall pigpiod`