#include <common.h>
#include <debug.h>
#include <elf.h>
#include <fcntl.h>
#include <stdio.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <unistd.h>
#include <utils.h>

typedef struct {
    char name[48];
    paddr_t addr;
    unsigned char info;
    Elf64_Xword size;
} SymbolTbl;

static int symbol_tbl_size = 0;
static SymbolTbl* symbol_tbl = NULL;

static void read_elf_header(int fd, Elf64_Ehdr* eh) {
    assert(lseek(fd, 0, SEEK_SET) == 0);
    assert(read(fd, (void*)eh, sizeof(Elf64_Ehdr)) == sizeof(Elf64_Ehdr));

    // check if is elf using fixed format of Magic: 7f 45 4c 46 ...
    if (strncmp((char*)eh->e_ident, "\177ELF", 4)) {
        panic("malformed ELF file");
    }
}

static void read_section(int fd, Elf64_Shdr sh, void* dst) {
    assert(dst != NULL);
    assert(lseek(fd, (off_t)sh.sh_offset, SEEK_SET) == (off_t)sh.sh_offset);
    assert(read(fd, dst, sh.sh_size) == sh.sh_size);
}

static void read_section_headers(int fd, Elf64_Ehdr eh, Elf64_Shdr* sh_tbl) {
    assert(lseek(fd, eh.e_shoff, SEEK_SET) == eh.e_shoff);
    for (int i = 0; i < eh.e_shnum; i++) {
        assert(read(fd, (void*)&sh_tbl[i], eh.e_shentsize) == eh.e_shentsize);
    }
}

static void read_symbol_table(int fd, Elf64_Ehdr eh, Elf64_Shdr sh_tbl[], int sym_idx) {
    Elf64_Sym sym_tbl[sh_tbl[sym_idx].sh_size];
    read_section(fd, sh_tbl[sym_idx], sym_tbl);

    int str_idx = sh_tbl[sym_idx].sh_link;
    char str_tbl[sh_tbl[str_idx].sh_size];
    read_section(fd, sh_tbl[str_idx], str_tbl);

    int sym_count = (sh_tbl[sym_idx].sh_size / sizeof(Elf64_Sym));
    // log
    Log("Symbol count: %d\n", sym_count);
    Log("====================================================\n");
    Log(" num    value            type size       name\n");
    Log("====================================================\n");
    for (int i = 0; i < sym_count; i++) {
        Log(" %-3d    %016lx %-4d %-10ld %s\n",
            i,
            sym_tbl[i].st_value,
            ELF64_ST_TYPE(sym_tbl[i].st_info),
            sym_tbl[i].st_size,
            str_tbl + sym_tbl[i].st_name);
    }
    Log("====================================================\n\n");

    // read
    symbol_tbl_size = sym_count;
    symbol_tbl = malloc(sizeof(SymbolTbl) * sym_count);
    for (int i = 0; i < sym_count; i++) {
        symbol_tbl[i].addr = sym_tbl[i].st_value;
        symbol_tbl[i].info = sym_tbl[i].st_info;
        symbol_tbl[i].size = sym_tbl[i].st_size;
        memset(symbol_tbl[i].name, 0, 32);
        strncpy(symbol_tbl[i].name, str_tbl + sym_tbl[i].st_name, 31);
    }
}

static void read_symbols(int fd, Elf64_Ehdr eh, Elf64_Shdr sh_tbl[]) {
    for (int i = 0; i < eh.e_shnum; i++) {
        switch (sh_tbl[i].sh_type) {
            case SHT_SYMTAB:
            case SHT_DYNSYM:
                read_symbol_table(fd, eh, sh_tbl, i);
                break;
        }
    }
}

// static int find_symbol_func(paddr_t target, bool is_call) {
//     int i;
//     for (i = 0; i < symbol_tbl_size; i++) {
//         if (ELF64_ST_TYPE(symbol_tbl[i].info) == STT_FUNC) {
//             if (is_call) {
//                 if (symbol_tbl[i].addr == target)
//                     break;
//             } else {
//                 if (symbol_tbl[i].addr <= target && target < symbol_tbl[i].addr + symbol_tbl[i].size)
//                     break;
//             }
//         }
//     }
//     return i < symbol_tbl_size ? i : -1;
// }

void parse_elf(const char* elf_file) {
    if (elf_file == NULL)
        return;

    Log("specified ELF file: %s", elf_file);
    int fd = open(elf_file, O_RDONLY | O_SYNC);

    Assert(fd >= 0, "Error %d: unable to open %s\n", fd, elf_file);

    Elf64_Ehdr eh;
    read_elf_header(fd, &eh);

    Elf64_Shdr sh_tbl[eh.e_shentsize * eh.e_shnum];
    read_section_headers(fd, eh, sh_tbl);

    read_symbols(fd, eh, sh_tbl);

    close(fd);
}
