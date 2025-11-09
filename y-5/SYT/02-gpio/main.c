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