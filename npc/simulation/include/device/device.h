#ifndef device_H
#define device_H

#include "common.h"

struct device {
    char* name;
    word_t low, high;
    uint8_t* space;
};

#endif