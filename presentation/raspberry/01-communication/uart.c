
#include <stdio.h>
#include <pigpio.h>

int main(int argc, char *argv[])
{
    if (gpioInitialise() < 0)
    {
        fprintf(stderr, "pigpio initialization failed\n");
        return 1;
    }

    // Open UART on /dev/serial0 with baud rate 115200
    int handle = serOpen("/dev/ttyAMA0", 9600, 0);
    if (handle < 0)
    {
        fprintf(stderr, "Failed to open UART\n");
        gpioTerminate();
        return 1;
    }

    // Example: write single characters
    /*
    const char *message = "12340";
    for (int i = 0; message[i] != '\0'; i++)
    {
        serWriteByte(handle, message[i]); // send one character
        gpioDelay(500000); // optional small delay (1 ms)
    }
    */

    for (int i = 48; i < 57 ; i++)
    {
        serWriteByte(handle, i); // send one character
        gpioDelay(500000); // optional small delay (1 ms)
    }

    // Close UART
    serClose(handle);
    gpioTerminate();
    return 0;
}
