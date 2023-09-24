#include <common.h>
#include <device.h>

#if defined(MULTIPROGRAM) && !defined(TIME_SHARING)
#define MULTIPROGRAM_YIELD() yield()
#else
#define MULTIPROGRAM_YIELD()
#endif

#define NAME(key) \
    [AM_KEY_##key] = #key,

static const char* keyname[256] __attribute__((used)) = {
    [AM_KEY_NONE] = "NONE",
    AM_KEYS(NAME)};

size_t serial_write(const void* buf, size_t offset, size_t len) {
    int ret = 0;
    for (ret = 0; ret < len; ret++) {
        putch(((char*)buf)[ret]);
    }
    return ret;
}

size_t events_read(void* buf, size_t offset, size_t len) {
    AM_INPUT_KEYBRD_T ev = io_read(AM_INPUT_KEYBRD);

    if (ev.keycode == AM_KEY_NONE) {
        *(char*)buf = '\0';
        return 0;
    }

    int ret = snprintf(buf, len, "%s %s\n", ev.keydown ? "kd" : "ku", keyname[ev.keycode]);
    return ret;
}

size_t dispinfo_read(void* buf, size_t offset, size_t len) {
    return snprintf(buf, len, "WIDTH: %d\nHEIGHT: %d", io_read(AM_GPU_CONFIG).width, io_read(AM_GPU_CONFIG).height);
}

size_t fb_write(const void* buf, size_t offset, size_t len) {
    int screen_w = io_read(AM_GPU_CONFIG).width;

    int draw_x = offset % screen_w;
    int draw_y = offset / screen_w;
    int draw_w = len >> 16;
    int draw_h = len & 0x0000ffff;

    io_write(AM_GPU_FBDRAW, draw_x, draw_y, (void*)buf, draw_h, draw_w, true);
    return len;
}

void init_device() {
    Log("Initializing devices...");
    ioe_init();
}
