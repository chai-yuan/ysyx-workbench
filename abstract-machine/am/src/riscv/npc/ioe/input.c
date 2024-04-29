#include <am.h>

#define PS2_BASE 0x10011000L
#define PS2(offset) (*(volatile char*)((PS2_BASE) + (offset)))

// #define AM_KEYS(_)
//   _(ESCAPE) _(F1) _(F2) _(F3) _(F4) _(F5) _(F6) _(F7) _(F8) _(F9) _(F10) _(F11) _(F12)
//   _(GRAVE) _(1) _(2) _(3) _(4) _(5) _(6) _(7) _(8) _(9) 24_(0) _(MINUS) _(EQUALS) _(BACKSPACE)
//   _(TAB) _(Q) _(W) _(E) _(R) _(T) _(Y) _(U) _(I) _(O) _(P) _(LEFTBRACKET) _(RIGHTBRACKET) _(BACKSLASH)
//   _(CAPSLOCK) _(A) _(S) _(D) _(F) _(G) _(H) _(J) _(K) _(L) _(SEMICOLON) _(APOSTROPHE) _(RETURN)
//   _(LSHIFT) _(Z) _(X) _(C) _(V) _(B) _(N) _(M) _(COMMA) _(PERIOD) _(SLASH) _(RSHIFT)
//   _(LCTRL) _(APPLICATION) _(LALT) _(SPACE) _(RALT) _(RCTRL)
//   _(UP) _(DOWN) _(LEFT) _(RIGHT) _(INSERT) _(DELETE) _(HOME) _(END) _(PAGEUP) _(PAGEDOWN)

static inline int keycode2amkey(const char code){
    switch(code){
      case 0x76 : return AM_KEY_ESCAPE;
      // F1-F12
      case 0x05 : return AM_KEY_F1;
      case 0x06 : return AM_KEY_F2;
      case 0x04 : return AM_KEY_F3;
      case 0x0c : return AM_KEY_F4;
      case 0x03 : return AM_KEY_F5;
      case 0x0b : return AM_KEY_F6;
      case 0x83 : return AM_KEY_F7;
      case 0x0a : return AM_KEY_F8;
      case 0x01 : return AM_KEY_F9;
      case 0x09 : return AM_KEY_F10;
      case 0x78 : return AM_KEY_F11;
      case 0x07 : return AM_KEY_F12;
      // 0-9
      case 0x16 : return AM_KEY_1;
      case 0x1e : return AM_KEY_2;
      case 0x26 : return AM_KEY_3;
      case 0x25 : return AM_KEY_4;
      case 0x2e : return AM_KEY_5;
      case 0x36 : return AM_KEY_6;
      case 0x3d : return AM_KEY_7;
      case 0x3e : return AM_KEY_8;
      case 0x46 : return AM_KEY_9;
      case 0x45 : return AM_KEY_0;
      // a-z
      case 0x15 : return AM_KEY_Q;
      case 0x1d : return AM_KEY_W;
      case 0x24 : return AM_KEY_E;
      case 0x2d : return AM_KEY_R;
      case 0x2c : return AM_KEY_T;
      case 0x35 : return AM_KEY_Y;
      case 0x3c : return AM_KEY_U;
      case 0x43 : return AM_KEY_I;
      case 0x44 : return AM_KEY_O;
      case 0x4d : return AM_KEY_P;

      case 0x1c : return AM_KEY_A;
      case 0x1b : return AM_KEY_S;
      case 0x23 : return AM_KEY_D;
      case 0x2b : return AM_KEY_F;
      case 0x34 : return AM_KEY_G;
      case 0x33 : return AM_KEY_H;
      case 0x3b : return AM_KEY_J;
      case 0x42 : return AM_KEY_K;
      case 0x4b : return AM_KEY_L;

      case 0x1a : return AM_KEY_Z;
      case 0x22 : return AM_KEY_X;
      case 0x21 : return AM_KEY_C;
      case 0x2a : return AM_KEY_V;
      case 0x32 : return AM_KEY_B;
      case 0x31 : return AM_KEY_N;
      case 0x3a : return AM_KEY_M;

      default : return AM_KEY_NONE;
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
