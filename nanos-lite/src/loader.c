#include <fs.h>
#include <loader.h>

#ifdef __LP64__
#define Elf_Ehdr Elf64_Ehdr
#define Elf_Phdr Elf64_Phdr
#else
#define Elf_Ehdr Elf32_Ehdr
#define Elf_Phdr Elf32_Phdr
#endif

static uintptr_t loader(PCB* pcb, const char* filename) {
    Elf_Ehdr ehdr;
    int file = fs_open(filename, 0, 0);

    fs_read(file, &ehdr, sizeof(Elf_Ehdr));
    assert((*(uint32_t*)ehdr.e_ident == 0x464c457f));  // check file type

    Elf_Phdr phdr[ehdr.e_phnum];
    fs_lseek(file, ehdr.e_phoff, SEEK_SET);
    fs_read(file, phdr, sizeof(Elf_Phdr) * ehdr.e_phnum);
    for (int i = 0; i < ehdr.e_phnum; i++) {
        if (phdr[i].p_type == PT_LOAD) {
            memset((void*)phdr[i].p_vaddr, 0, phdr[i].p_memsz);  // memset 0
            fs_lseek(file, phdr[i].p_offset, SEEK_SET);
            fs_read(file, (void*)phdr[i].p_vaddr, phdr[i].p_filesz);
        }
    }
    fs_close(file);
    return ehdr.e_entry;
}

void naive_uload(PCB* pcb, const char* filename) {
    uintptr_t entry = loader(pcb, filename);
    Log("Jump to entry = %p", entry);
    ((void (*)())entry)();
}
