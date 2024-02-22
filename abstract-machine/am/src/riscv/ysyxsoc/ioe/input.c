#include <am.h>

#define PS2_BASE 0x10011000L
#define PS2(offset) (*(volatile char*)((PS2_BASE) + (offset)))

static inline int keycode2amkey(const char code){
    switch(code){
      case 0x76 : return 1;
      case 0x05 : return 2;
      case 0x06 : return 3;
      case 0x04 : return 4;
      case 0x0c : return 5;
      default : return code;
    }
}

void __am_input_keybrd(AM_INPUT_KEYBRD_T *kbd) {
    char data = PS2(0);

    if(data == 0){
        kbd->keydown = 0;
        kbd->keycode = AM_KEY_NONE;
    } else {

        if(data == 0xf0){ // 松开按键
          kbd->keydown = 0;
          do{data = PS2(0);}while(data == 0); // 读取松开的按键keycode
        }else{            // 按下
          kbd->keydown = 1;
        }

        kbd->keycode = keycode2amkey(data);
    }
}
