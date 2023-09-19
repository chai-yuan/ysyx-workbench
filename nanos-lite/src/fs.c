#include <device.h>
#include <fs.h>
#include <ramdisk.h>

typedef size_t (*ReadFn)(void* buf, size_t offset, size_t len);
typedef size_t (*WriteFn)(const void* buf, size_t offset, size_t len);

typedef struct {
    char* name;
    size_t size;
    size_t disk_offset;
    ReadFn read;
    WriteFn write;
    // -
    size_t open_offset;
} Finfo;

enum { FD_STDIN,
       FD_STDOUT,
       FD_STDERR,
       FD_FB };

size_t invalid_read(void* buf, size_t offset, size_t len) {
    panic("should not reach here");
    return 0;
}

size_t invalid_write(const void* buf, size_t offset, size_t len) {
    panic("should not reach here");
    return 0;
}

/* This is the information about all files in disk. */
static Finfo file_table[] __attribute__((used)) = {
    [FD_STDIN] = {"stdin", 0, 0, invalid_read, invalid_write},
    [FD_STDOUT] = {"stdout", 0, 0, invalid_read, serial_write},
    [FD_STDERR] = {"stderr", 0, 0, invalid_read, serial_write},
#include "files.h"
};

void init_fs() {
    // TODO: initialize the size of /dev/fb
}

int fs_open(const char* pathname, int flags, int mode) {
    for (int i = 0; i < LENGTH(file_table); i++) {
        if (strcmp(pathname, file_table[i].name) == 0) {
            return i;
        }
    }
    return -1;
}

size_t fs_read(int fd, void* buf, size_t len) {
    Finfo* file = &file_table[fd];
    size_t ret = 0;

    if (file->read == NULL) {
        size_t read_len = (file->size - file->open_offset) < len ? (file->size - file->open_offset) : len;
        ret = ramdisk_read(buf, file->disk_offset + file->open_offset, read_len);
        file->open_offset += ret;
    } else {
        ret = file->read(buf, file->open_offset, len);
        file->open_offset += ret;
    }
    return ret;
}

size_t fs_write(int fd, const void* buf, size_t len) {
    Finfo* file = &file_table[fd];
    size_t ret = 0;

    if (file->write == NULL) {
        size_t write_len = (file->size - file->open_offset) < len ? (file->size - file->open_offset) : len;
        ret = ramdisk_write(buf, file->disk_offset + file->open_offset, write_len);
        file->open_offset += ret;
    } else {
        ret = file->write(buf, file->open_offset, len);
        file->open_offset += ret;
    }
    return ret;
}

size_t fs_lseek(int fd, size_t offset, int whence) {
    Finfo* file = &file_table[fd];

    switch (whence) {
        case SEEK_SET:
            file->open_offset = offset;
            break;
        case SEEK_CUR:
            file->open_offset += offset;
            break;
        case SEEK_END:
            file->open_offset = file->size -= offset;
            break;
        default:
            panic("unknown whence");
    }

    return file->open_offset;
}

int fs_close(int fd) {
    return 0;
}

const char* fd_to_filename(int fd) {
    return file_table[fd].name;
}