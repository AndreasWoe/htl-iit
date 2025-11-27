#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

//Sensor-ID anpassen!
#define SENSOR_PATH "/sys/bus/w1/devices/28-xxxxxxxxxxxx/w1_slave"

double read_temperature() {
    FILE *fp = fopen(SENSOR_PATH, "r");
    if (fp == NULL) {
        perror("Fehler beim Öffnen der Datei");
        return -999.0;
    }

    char buffer[256];
    double tempC = -999.0;

    while (fgets(buffer, sizeof(buffer), fp)) {
        char *t_ptr = strstr(buffer, "t=");
        if (t_ptr) {
            tempC = atof(t_ptr + 2) / 1000.0; // Umrechnung in °C
            break;
        }
    }

    fclose(fp);
    return tempC;
}

int main() {
    while (1) {
        double temp = read_temperature();
        if (temp != -999.0) {
            printf("Temperatur: %.3f °C\n", temp);
        } else {
            printf("Fehler beim Lesen der Temperatur!\n");
        }
        sleep(1); // 1 Sekunde warten
    }
    return 0;
}