#include <am.h>
#include <klib-macros.h>
#include <klib.h>
#include <stdarg.h>

#if !defined(__ISA_NATIVE__) || defined(__NATIVE_USE_KLIB__)

int printf(const char* fmt, ...) {
    char buf[4096];
    va_list ap;
    va_start(ap, fmt);
    int retval = vsprintf(buf, fmt, ap);
    va_end(ap);
    putstr(buf);
    return retval;
}

int vsprintf(char* out, const char* fmt, va_list ap) {
    char* str;
    for (str = out; *fmt; fmt++) {
        if (*fmt != '%') {
            *str++ = *fmt;
            continue;
        }

        fmt++;
        switch (*fmt) {
            case 'p':	
            case 'd': {
                int i = va_arg(ap, int);
                char buffer[12];  // Buffer to hold int as string
                itoa(i, buffer, 10);
                strcpy(str, buffer);
                str += strlen(buffer);
                break;
            }
            case 's': {
                char* s = va_arg(ap, char*);
                strcpy(str, s);
                str += strlen(s);
                break;
            }
            default:
                *str++ = *fmt;
                break;
        }
    }

    *str = '\0';
    return str - out;
}

int sprintf(char* out, const char* fmt, ...) {
    va_list ap;
    va_start(ap, fmt);
    int retval = vsprintf(out, fmt, ap);
    va_end(ap);
    return retval;
}

int snprintf(char* out, size_t n, const char* fmt, ...) {
    va_list ap;
    va_start(ap, fmt);
    int retval = vsnprintf(out, n, fmt, ap);
    va_end(ap);
    return retval;
}

int vsnprintf(char* out, size_t n, const char* fmt, va_list ap) {
    char* str;
    for (str = out; *fmt; fmt++) {
        if (*fmt != '%') {
            if (str - out < n) {
                *str++ = *fmt;
            }
            continue;
        }

        fmt++;
        switch (*fmt) {
            case 'p':
            case 'd': {
                int i = va_arg(ap, int);
                char buffer[12];  // Buffer to hold int as string
                itoa(i, buffer, 10);
                if (str - out < n) {
                    strncpy(str, buffer, n - (str - out));
                    str += strlen(buffer);
                }
                break;
            }
            case 's': {
                char* s = va_arg(ap, char*);
                if (str - out < n) {
                    strncpy(str, s, n - (str - out));
                    str += strlen(s);
                }
                break;
            }
            default:
                if (str - out < n) {
                    *str++ = *fmt;
                }
                break;
        }
    }

    *str = '\0';
    return str - out;
}

#endif
