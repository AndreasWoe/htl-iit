# ATmega 2560 UART

## Der ATmega2560 verfügt über 4 UART-Schnittstellen:
- USART0 → Register: UCSR0A, UCSR0B, UCSR0C, UDR0, UBRR0H, UBRR0L
- USART1 → Register: UCSR1A, UCSR1B, UCSR1C, UDR1, UBRR1H, UBRR1L
- USART2 → Register: UCSR2A, UCSR2B, UCSR2C, UDR2, UBRR2H, UBRR2L
- USART3 → Register: UCSR3A, UCSR3B, UCSR3C, UDR3, UBRR3H, UBRR3L

UART0 ist dabei über einen UART <-> USB Converter mit dem Rechner verbunden und kann daher in der Arduino IDE im Serial Monitor genutzt werden.

## Bedeutung der Register:
- UCSRnA, UCSRnB, UCSRnC: Steuerregister für USARTn
- UDRn: Datenregister für Senden/Empfangen
- UBRRnH, UBRRnL: Baudratenregister (High/Low Byte)

## Notwendige Schritte zum Initalisieren der Schnittstelle (TX)

### Baudratenregister setzen (UBRR0H, UBRR0L)
In diesen Registern müssen wir dem UART mitteilen, wie schnell wir gerne kommunizieren möchten. Der Wert, der in dieses Register geschrieben werden muss, errechnet sich nach folgender Formel:

(Taktfrequenz in Hz / (Baudrate * 16)) - 1
Die Taktfrequenz unserer ATmega 2560 Boards beträgt 16MHz. Wir können dafür folgende Konstante definieren:

`#define CPU_FREQ 16000000UL //unsigned long = 4byte`

Es sind Baudraten bis über 115200 Baud möglich, je nach Controller und CPU-Frequenz.

Siehe Datenblatt - 22.10.5 UBRRnL and UBRRnH – USART Baud Rate Registers

### Transmitter aktivieren (UCSR0B)
Siehe Datenblatt - 22.10.3 UCSRnB – USART Control and Status Register n B

### Konfigurationsregister setzen (UCSR0C)
Folgende Konfiguration soll gesetzt werden:

`Asynchroner Modus, keine Parität, 1 Stopbit, 8 Datenbits`

Siehe Datenblatt - 22.10.4 UCSRnC – USART Control and Status Register n C

### Prüfen ob Senderegister leer ist (UCSR0A)
Vor dem Senden muss geprüft/gewartet werden, ob/bis die Schnittstelle bereit ist. 

Siehe Datenblatt - 22.10.2 UCSRnA – USART Control and Status Register A

### Ein Zeichen senden (UDR0)
Siehe Datenblatt - 22.10.1 UDRn – USART I/O Data Register n





