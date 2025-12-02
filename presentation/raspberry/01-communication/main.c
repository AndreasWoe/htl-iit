#include <stdio.h>
#include <pigpio.h>

#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <net/if.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <linux/can.h>
#include <linux/can/raw.h>

int main(int argc, char *argv[])
{
    int s;
    struct sockaddr_can addr;
    struct ifreq ifr;
    struct can_frame frame;

    // Create CAN RAW socket
    s = socket(PF_CAN, SOCK_RAW, CAN_RAW);
    if (s < 0) {
        perror("Socket");
        return 1;
    }

    // Specify CAN interface (can0)
    strcpy(ifr.ifr_name, "can0");
    ioctl(s, SIOCGIFINDEX, &ifr);

    addr.can_family = AF_CAN;
    addr.can_ifindex = ifr.ifr_ifindex;

    // Bind socket to can0
    if (bind(s, (struct sockaddr *)&addr, sizeof(addr)) < 0) { perror("Bind"); }

    printf("Listening on can0...\n");

    //////////////////////////////////
    //UART
    //////////////////////////////////

    if (gpioInitialise() < 0)
    {
        fprintf(stderr, "pigpio initialization failed\n");
        return 1;
    }

    // Open UART
    int handle = serOpen("/dev/ttyAMA0", 9600, 0);
    if (handle < 0)
    {
        fprintf(stderr, "Failed to open UART\n");
        gpioTerminate();
        return 1;
    }

    // Receive CAN frames
    while (1) {
        int nbytes = read(s, &frame, sizeof(struct can_frame));
        if (nbytes < 0) {
            perror("Read");
            break;
        }

        printf("ID: 0x%X DLC: %d Data:", frame.can_id, frame.can_dlc);
        for (int i = 0; i < frame.can_dlc; i++) {
            printf(" %02X", frame.data[i]);
	    // convert to ASCII & send to UART
	    serWriteByte(handle, frame.data[i] + 48); 
        }
        printf("\n");
    }

    // Close CAN
    close(s);

    // Close UART
    serClose(handle);
    gpioTerminate();
    return 0;
}
