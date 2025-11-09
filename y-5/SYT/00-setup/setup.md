# Raspberry Pi 2 aufsetzen für C Entwicklung

## Betriebssystem flashen
- Raspberry Pi Imager laden und SD Karte flashen
- https://www.raspberrypi.com/software/

Modell: Raspberry Pi 2 Model B
Betriebssystem (OS): Raspberry Pi OS (32Bit)

### Voreinstellung
- Hostname z.B. `rpi-XX`
- Standard Benutzername / Passwort (pi / raspberry)
- WLAN: RaspberryLab / raspberry

## (optional) Hostname in GUI konfigurieren / kontrollieren

Start / Preferences / Raspberry Pi Configuration / System / Hostname

z.B. `rpi-22`

## TigerVNC (Steuerung Raspberry Pi über WLAN)
Download Tiger VNC

https://sourceforge.net/projects/tigervnc/files/stable/1.15.0/

- VNC-Server: z.B. `rpi-22`
- Optionen / Eingabe / Lokalen Cursor anzeigen ... - aktivieren

## WinSCP (File Transfern PC <-> Raspberry Pi über WLAN)
Download WinSCP

https://winscp.net/eng/download.php

- Übertragungsprotokoll: SCP
- Serveradresse: z.B. `rpi-22`

## C "HelloWorld" kompilieren und ausführen

Startmenü / Entwicklung / Geany

```
#include <stdio.h>

int main(){
   printf("Hello, World! \n");
   return 0;
}

```

- Erstellen / Compile -> Objektdatei wird erstellt
- Erstellen / Build -> ausführbare Datei wird erstellt
- Erstellen / Execute -> Programm wird in der Konsole ausgeführt
