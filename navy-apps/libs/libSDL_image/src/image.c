#define SDL_malloc malloc
#define SDL_free free
#define SDL_realloc realloc

#define SDL_STBIMAGE_IMPLEMENTATION
#include "SDL_stbimage.h"

SDL_Surface* IMG_Load_RW(SDL_RWops* src, int freesrc) {
    assert(src->type == RW_TYPE_MEM);
    assert(freesrc == 0);
    return NULL;
}

SDL_Surface* IMG_Load(const char* filename) {
    // 用libc中的文件操作打开文件, 并获取文件大小size
    FILE* image_file = fopen(filename, "r");
    assert(image_file);
    fseek(image_file, 0, SEEK_END);
    int image_size = ftell(image_file);
    // 申请一段大小为size的内存区间buf
    uint8_t* buf = SDL_malloc(image_size);
    assert(buf != NULL);
    // 将整个文件读取到buf中
    fseek(image_file, 0, SEEK_SET);
    fread(buf, image_size, 1, image_file);
    // 将buf和size作为参数, 调用STBIMG_LoadFromMemory(), 它会返回一个SDL_Surface结构的指针
    SDL_Surface* ret = STBIMG_LoadFromMemory(buf, image_size);
    assert(ret != NULL);
    // 关闭文件, 释放申请的内存
    fclose(image_file);
    free(buf);
    return ret;
}

int IMG_isPNG(SDL_RWops* src) {
    return 0;
}

SDL_Surface* IMG_LoadJPG_RW(SDL_RWops* src) {
    return IMG_Load_RW(src, 0);
}

char* IMG_GetError() {
    return "Navy does not support IMG_GetError()";
}
