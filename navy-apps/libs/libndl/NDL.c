#include <assert.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/time.h>
#include <time.h>
#include <unistd.h>

static int evtdev = -1;
static int fbdev = -1;
static int screen_w = 0, screen_h = 0;

uint32_t NDL_GetTicks() {
    struct timeval current_time;
    gettimeofday(&current_time, NULL);
    return current_time.tv_sec * 1000 + current_time.tv_usec / 1000;
}

int NDL_PollEvent(char* buf, int len) {
    int event_file = open("/dev/events", 0, 0);
    int ret = read(event_file, buf, len);
    close(event_file);
    return ret;
}

void NDL_OpenCanvas(int* w, int* h) {
    // 获取屏幕信息
    int dispinfo = open("/proc/dispinfo", 0, 0);
    char dispinfo_s[32];
    read(dispinfo, dispinfo_s, 32);
    sscanf(dispinfo_s, "WIDTH: %d\nHEIGHT: %d", &screen_w, &screen_h);

    close(dispinfo);
    if (*w == 0 || *h == 0) {
        *w = screen_w;
        *h = screen_h;
    }

    if (getenv("NWM_APP")) {
        int fbctl = 4;
        fbdev = 5;
        screen_w = *w;
        screen_h = *h;
        char buf[64];
        int len = sprintf(buf, "%d %d", screen_w, screen_h);
        // let NWM resize the window and create the frame buffer
        write(fbctl, buf, len);
        while (1) {
            // 3 = evtdev
            int nread = read(3, buf, sizeof(buf) - 1);
            if (nread <= 0)
                continue;
            buf[nread] = '\0';
            if (strcmp(buf, "mmap ok") == 0)
                break;
        }
        close(fbctl);
    }
}

void NDL_DrawRect(uint32_t* pixels, int x, int y, int w, int h) {
    assert(pixels);

    int fb = open("/dev/fb", 0, 0);
    lseek(fb, (x * y), SEEK_SET);
    // 压缩到32位传输高和宽
    write(fb, pixels, (w << 16) | (h & 0x0000ffff));
    close(fb);
}

void NDL_OpenAudio(int freq, int channels, int samples) {
}

void NDL_CloseAudio() {
}

int NDL_PlayAudio(void* buf, int len) {
    return 0;
}

int NDL_QueryAudio() {
    return 0;
}

int NDL_Init(uint32_t flags) {
    if (getenv("NWM_APP")) {
        evtdev = 3;
    }

    return 0;
}

void NDL_Quit() {
}
