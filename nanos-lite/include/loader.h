#ifndef loader_H
#define loader_H

#include <elf.h>
#include <proc.h>
#include <ramdisk.h>

void naive_uload(PCB* pcb, const char* filename);

#endif