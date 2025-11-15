# Raspberry Pi Cheatsheet

## Home Directory zurücksetzen

`sudo rm -rf /home/pi/`

- sudo : superuser do
- rm : remove
 - -r: Löscht Verzeichnisse und deren Inhalt rekursiv.
- -f: Erzwingt das Löschen, ohne eine Bestätigung anzufordern.

## Git

`git clone ...`

... danach in das Repository Verzeichnis wechseln ...

`cd ...`

... und folgendes ausführen:

```
git config user.name "Max Mustermann"
git config user.email max.mustermann@htl-wels.at
```

## Raspberry Pi

Raspberry Pi über Terminal ausschalten/neu starten

```
sudo poweroff
sudo reboot
```