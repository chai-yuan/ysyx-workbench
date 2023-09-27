#include "syscall.h"
#include <common.h>
#include <fs.h>
#include <loader.h>
#include <nanos-config.h>

struct timeval {
    long tv_sec;  /* seconds */
    long tv_usec; /* microseconds */
};
struct timezone {
    int tz_minuteswest; /* Minutes west of GMT.  */
    int tz_dsttime;     /* Nonzero if DST is ever in effect.  */
};

int sys_yield() {
#ifdef CONFIG_STRACE
    Log("yield() -> void");
#endif
    yield();
    return 0;
}

int sys_write(int fd, void* buf, size_t count) {
    int ret = fs_write(fd, buf, count);
#ifdef CONFIG_STRACE
    Log("write(%s,%d,%d) -> %d", fd_to_filename(fd), buf, count, ret);
#endif
    return ret;
}

int sys_brk(void* addr) {
    int ret = 0;
#ifdef CONFIG_STRACE
    Log("brk(%p) -> %d", addr, ret);
#endif
    return ret;
}

int sys_open(char* pathname, int flags, int mode) {
    int ret = fs_open(pathname, flags, mode);
#ifdef CONFIG_STRACE
    Log("open(%s,%d,%d) -> %d", pathname, flags, mode, ret);
#endif
    return ret;
}

size_t sys_read(int fd, void* buf, size_t len) {
    int ret = fs_read(fd, buf, len);
#ifdef CONFIG_STRACE
    Log("read(%s,%d,%d) -> %d", fd_to_filename(fd), buf, len, ret);
#endif
    return ret;
}

size_t sys_lseek(int fd, size_t offset, int whence) {
    int ret = fs_lseek(fd, offset, whence);
#ifdef CONFIG_STRACE
    Log("lseek(%s,%d,%d) -> %d", fd_to_filename(fd), offset, whence, ret);
#endif
    return ret;
}

int sys_close(int fd) {
    int ret = fs_close(fd);
#ifdef CONFIG_STRACE
    Log("close(%s) -> %d", fd_to_filename(fd), ret);
#endif
    return ret;
}

int sys_gettimeofday(struct timeval* tv, struct timezone* tz) {
    uint64_t us = io_read(AM_TIMER_UPTIME).us;
    if (tv != NULL) {
        tv->tv_sec = us / (1000 * 1000);
        tv->tv_usec = us % (1000 * 1000);
    }
    if (tz != NULL) {
    }
#ifdef CONFIG_STRACE
    Log("gettimeofday(%p,%p) -> %d", tv, tz, 0);
#endif
    return 0;
}

int sys_execve(const char* filename, char* const argv[], char* const envp[]) {
    naive_uload(NULL, filename);
#ifdef CONFIG_STRACE
    Log("execve(%s,%p,%p) -> %d", filename, argv, envp, 0);
#endif
    return 0;
}

int sys_exit(int code) {
#ifdef CONFIG_STRACE
    Log("halt(%d) -> void", code);
#endif
    sys_execve("/bin/nterm", NULL, NULL);
    // halt(code);
    return 0;
}

void do_syscall(Context* c) {
    uintptr_t a[4];
    a[0] = c->GPR1;

    switch (a[0]) {
        case SYS_exit:
            c->GPRx = sys_exit(c->GPR2);
            break;
        case SYS_yield:
            c->GPRx = sys_yield();
            break;
        case SYS_open:
            c->GPRx = sys_open((char*)c->GPR2, c->GPR3, c->GPR4);
            break;
        case SYS_write:
            c->GPRx = sys_write(c->GPR2, (void*)c->GPR3, c->GPR4);
            break;
        case SYS_read:
            c->GPRx = sys_read(c->GPR2, (void*)c->GPR3, c->GPR4);
            break;
        case SYS_lseek:
            c->GPRx = sys_lseek(c->GPR2, c->GPR3, c->GPR4);
            break;
        case SYS_close:
            c->GPRx = sys_close(c->GPR2);
            break;
        case SYS_gettimeofday:
            c->GPRx = sys_gettimeofday((struct timeval*)c->GPR2, (struct timezone*)c->GPR3);
            break;
        case SYS_brk:
            c->GPRx = sys_brk((void*)c->GPR2);
            break;
        case SYS_execve:
            c->GPRx = sys_execve((char*)c->GPR2, (char* const*)c->GPR3, (char* const*)c->GPR4);
            break;
        default:
            panic("Unhandled syscall ID = %d", a[0]);
    }
}