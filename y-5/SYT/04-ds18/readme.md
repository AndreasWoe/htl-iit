# DS18 Temperatursensor

## Aufgabe 1 - Testen der Sensoren in der Konsole

### Ausführen:
```
sudo modprobe w1_gpio
sudo modprobe w1_therm
```

### Prüfen & ID des Sensors auslesen:
`ls /sys/bus/w1/devices/`

Antwort z.B.: `28-01144fdd30aa  w1_bus_master1`

### Temperatur auslesen:
`cat /sys/bus/w1/devices/28-xxxxxxxxx/w1_slave`

Replace 28-xxxxxxxxx with the actual serial number of your sensor. The output will contain a line with "YES" for CRC check, and the temperature will be shown on the second line (e.g., t=25000 which is 25.0 C)

### Information zum Sensor:
https://www.elektronik-kompendium.de/sites/praxis/bauteil_ds18b20.htm

## Aufgabe 2 - Implementierung in C
Die Daten sollen nun im C Programm erfasst und an den Webserver übertragen werden. Ein Beispielprogramm zum Auslesen der Daten findest du im Anhang.