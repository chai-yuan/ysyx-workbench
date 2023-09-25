#ifndef __SDL_EVENT_H__
#define __SDL_EVENT_H__

#define _KEYS(_)                                                                                             \
    _(ESCAPE)                                                                                                \
    _(F1)                                                                                                    \
    _(F2)                                                                                                    \
    _(F3)                                                                                                    \
    _(F4)                                                                                                    \
    _(F5)                                                                                                    \
    _(F6)                                                                                                    \
    _(F7)                                                                                                    \
    _(F8)                                                                                                    \
    _(F9)                                                                                                    \
    _(F10)                                                                                                   \
    _(F11)                                                                                                   \
    _(F12)                                                                                                   \
    _(GRAVE)                                                                                                 \
    _(1) _(2) _(3) _(4) _(5) _(6) _(7) _(8) _(9) _(0) _(MINUS) _(EQUALS) _(BACKSPACE)                        \
        _(TAB) _(Q) _(W) _(E) _(R) _(T) _(Y) _(U) _(I) _(O) _(P) _(LEFTBRACKET) _(RIGHTBRACKET) _(BACKSLASH) \
            _(CAPSLOCK) _(A) _(S) _(D) _(F) _(G) _(H) _(J) _(K) _(L) _(SEMICOLON) _(APOSTROPHE) _(RETURN)    \
                _(LSHIFT) _(Z) _(X) _(C) _(V) _(B) _(N) _(M) _(COMMA) _(PERIOD) _(SLASH) _(RSHIFT)           \
                    _(LCTRL) _(APPLICATION) _(LALT) _(SPACE) _(RALT) _(RCTRL)                                \
                        _(UP) _(DOWN) _(LEFT) _(RIGHT) _(INSERT) _(DELETE) _(HOME) _(END) _(PAGEUP) _(PAGEDOWN)

#define enumdef(k) SDLK_##k,

enum SDL_Keys {
    SDLK_NONE = 0,
    _KEYS(enumdef)
};

enum SDL_EventType {
    SDL_KEYDOWN,
    SDL_KEYUP,
    SDL_USEREVENT,  // 用户自定义事件
};

#define SDL_EVENTMASK(ev_type) (1u << (ev_type))

enum SDL_EventAction {
    SDL_ADDEVENT,
    SDL_PEEKEVENT,
    SDL_GETEVENT,
};

typedef struct {
    uint8_t sym;
} SDL_keysym;

typedef struct {
    uint8_t type;       // 事件类型SDL_KEYDOWN或SDL_KEYUP
    SDL_keysym keysym;  // SDL_Keysym表示按下或释放的键
} SDL_KeyboardEvent;

typedef struct {
    uint8_t type;
    int code;
    void* data1;
    void* data2;
} SDL_UserEvent;

typedef union {
    uint8_t type;
    SDL_KeyboardEvent key;
    SDL_UserEvent user;
} SDL_Event;

/*这个函数用于将一个事件推送到事件队列中。
 * 这个事件会被放在队列的末尾，并在调用SDL_PollEvent或SDL_WaitEvent时被处理。
 * 如果事件队列已满，此函数将失败。
 */
int SDL_PushEvent(SDL_Event* ev);

/*这个函数用于检查事件队列中是否有事件。
 *如果有，它会从队列中删除该事件并返回1，
 *如果没有，它会返回0。
 *这个函数不会阻塞，如果没有事件可处理，它会立即返回
 */
int SDL_PollEvent(SDL_Event* ev);

/*这个函数与SDL_PollEvent类似，
 * 但如果事件队列中没有事件，它会阻塞，
 * 直到有事件可处理。
 */
int SDL_WaitEvent(SDL_Event* ev);

/*这个函数用于查看或直接处理事件队列中的事件。
 *它可以用于添加、读取或更新事件，或者删除满足特定条件的事件。
 */
int SDL_PeepEvents(SDL_Event* ev, int numevents, int action, uint32_t mask);

/*这个函数返回一个数组，表示当前的键盘状态。
 * 数组的每个元素对应一个键，如果键被按下，元素的值为1，否则为0。
 * 这个函数可以用于检查在某个时刻哪些键被按下。
 */
uint8_t* SDL_GetKeyState(int* numkeys);

#endif
