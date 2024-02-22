#include <am.h>
#include <stdio.h>

#define VGA_BASE 0x21000000L
#define VGA(w,h) (*(volatile uint32_t*)((VGA_BASE) + (w << 11) + (h << 2)))
#define VGA_W 640
#define VGA_H 480

void __am_gpu_init() {
    // 初始填充一些颜色
    for(int i=0;i<VGA_W;i+=3){
        for(int j=0;j<VGA_H;j+=3){
            VGA(i,j) = (i*j);
        }
    }
}

void __am_gpu_config(AM_GPU_CONFIG_T* cfg) {
    *cfg = (AM_GPU_CONFIG_T){
        .present = true, .has_accel = false, .width = VGA_W, .height = VGA_H, .vmemsz = 0};
}

void __am_gpu_fbdraw(AM_GPU_FBDRAW_T* ctl) {
    int x = ctl->x, y = ctl->y, w = ctl->w, h = ctl->h;
    uint32_t* pixels = ctl->pixels;

    for (int i = y; i < y + h; i++) {
        for (int j = x; j < x + w; j++) {
            VGA(j,i) = pixels[w * (i - y) + (j - x)];
        }
    }
}

void __am_gpu_status(AM_GPU_STATUS_T* status) {
    status->ready = true;
}
