#include <am.h>
#include <nemu.h>
#include <stdio.h>

#define KEYDOWN_MASK 0x8000

void __am_input_keybrd(AM_INPUT_KEYBRD_T *kbd) {
  uint32_t key_code = inl(KBD_ADDR);
  // printf("%d\n",key_code);
  
  kbd->keydown = key_code & KEYDOWN_MASK ? true : false;
  kbd->keycode = key_code & ~KEYDOWN_MASK;
}
