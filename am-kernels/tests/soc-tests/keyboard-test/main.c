#include "trap.h"

int main() {
    ioe_init();
    printf("key test!\ntype some char:\n");

    AM_INPUT_KEYBRD_T key_data;

    while(1){
        do{
            ioe_read(AM_INPUT_KEYBRD,&key_data);
        }while(key_data.keycode == AM_KEY_NONE);

        printf("data : %d\n",key_data.keycode);
    }

    halt(0);
}
