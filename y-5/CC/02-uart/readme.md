# ATmega 2560 UART

https://www.mikrocontroller.net/articles/AVR-GCC-Tutorial/Der_UART

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
