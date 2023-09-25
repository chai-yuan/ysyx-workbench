#include <NDL.h>
#include <SDL.h>
#include <string.h>

#define keyname(k) #k,

static uint8_t key_states[256];

static const char* keyname[] = {
    "NONE",
    _KEYS(keyname)};

int SDL_PushEvent(SDL_Event* ev) {
    assert(0);
    return 0;
}

static int keyname2code(const char* name) {
    for (int i = 1; i < (sizeof(keyname) / sizeof(keyname[0])); i++) {
        if (strcmp(keyname[i], name) == 0)
            return i;
    }
    return 0;  // NONE
}

int SDL_PollEvent(SDL_Event* ev) {
    // 从hdl获得事件
    char hdl_buf[32];
    int hdl_ret = NDL_PollEvent(hdl_buf, sizeof(hdl_buf));
    assert(hdl_ret >= 0);

    // 处理事件
    char key_type[3], key_name[8];
    if (hdl_ret == 0) {
        return 0;
    } else {
        sscanf(hdl_buf, "%s %s", &key_type, &key_name);
        int key_code = keyname2code(key_name);

        if (strcmp(key_type, "kd") == 0) {
            ev->key.keysym.sym = key_code;
            ev->type = ev->key.type = SDL_KEYDOWN;
            key_states[key_code] = 1;
        } else if (strcmp(key_type, "ku")) {
            ev->key.keysym.sym = key_code;
            ev->type = ev->key.type = SDL_KEYUP;
            key_states[key_code] = 0;
        }

        return 1;
    }
    return -1;
}

int SDL_WaitEvent(SDL_Event* event) {
    while (SDL_PollEvent(event) != 1)
        ;
    if (event == NULL) {
        assert(0);
        return 0;  // error
    }
    return 1;
}

int SDL_PeepEvents(SDL_Event* ev, int numevents, int action, uint32_t mask) {
    assert(0);
    return 0;
}

uint8_t* SDL_GetKeyState(int* numkeys) {
    if (numkeys != NULL)
        *numkeys = (int)(sizeof(keyname) / sizeof(keyname[0]));
    return key_states;
}
