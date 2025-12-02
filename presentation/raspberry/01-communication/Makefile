all: main.c
	gcc main.c -o main -lpigpio -lrt -lpthread
	
uart: uart.c
	gcc uart.c -o uart -lpigpio -lrt -lpthread
	
canstart:
	sudo ip link set can0 up type can bitrate 500000
	ifconfig can0
	candump can0