#include "trap.h"

int main() {
    ioe_init();
    printf("uart test!\ntype some char:\n");

    AM_UART_RX_T rx_data;

    while(1){
        do{
            ioe_read(AM_UART_RX,&rx_data);
        }while(rx_data.data == 0xff);

        printf("rx_data : %d\n",rx_data.data);
    }

    halt(0);
}
