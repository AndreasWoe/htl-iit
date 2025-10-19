#include <avr/io.h>
#include <util/delay.h>
 
int main(void)
{
  //Port H Pin 4 -> Arduino Pin 7
  //Port H Pin 5 -> Arduino Pin 8
  DDRH |= (1<<4);       //Pin 4 Port H -> OUTPUT
  PORTH |= (1<<5);      //set Bit 5 in PORTH Register -> internal pullup ACTIVE
 
  while (1) {
    if(!(PINH & (1<<5))) { //0 == FALSE all other values are TRUE
      PORTH |= (1<<4);  //Pin 4 Port H -> set HIGH
      _delay_ms(200);
      PORTH &= ~(1<<4); //Pin 4 Port H -> set LOW
      _delay_ms(200);  
    }
  }
 
  return 0;
}
