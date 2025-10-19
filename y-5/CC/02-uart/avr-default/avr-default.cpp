#include <avr/io.h>
#include <util/delay.h> 

#define CPU_FREQ 16000000UL     // CPU-Frequenz
#define BAUD 9600            // Baudrate
#define UBRR_VALUE ((CPU_FREQ / (16UL * BAUD)) - 1)

void uart_init(void) {
    // Baudrate setzen
    UBRR0H = (unsigned char)(UBRR_VALUE >> 8);
    UBRR0L = (unsigned char)UBRR_VALUE;

    // Transmitter aktivieren
    UCSR0B = (1 << 3);

    // Asynchroner Modus, keine Parität, 1 Stopbit, 8 Datenbits
    UCSR0C = (1 << UCSZ01) | (1 << UCSZ00);
}

void uart_send_char(char c) {
    // Warten bis das Senderegister leer ist
    while (!(UCSR0A & (1 << UDRE0)));
    // Zeichen senden
    UDR0 = c;
}

int main(void) {
    uart_init();         // UART initialisieren
    

    while (1) {
        // Endlosschleife
        uart_send_char('A'); // Zeichen senden
        _delay_ms(1000);
    }

    return 0;
}
