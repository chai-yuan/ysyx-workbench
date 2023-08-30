#include <common.h>
#include <debug.h>
#include <elf.h>
#include <fcntl.h>
#include <stdio.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <unistd.h>
#include <utils.h>

#define FUNC_NUM 256

FuncItem elf_func[FUNC_NUM];

void parse_elf(const char* elf_file) {
    Log("Specify elf file : %s", elf_file);

    FILE* fp = read_check_elf(elf_file);
    Elf64_Ehdr elf_head = read_elf_head(fp);
    read_elf_section(fp, &elf_head);

    for (int i = 0; i < 8; i++) {
        printf("%s\n%d : %d\n", elf_func[i].func_name, elf_func[i].start, elf_func[i].size);
    }
}

FILE* read_check_elf(const char* elf_file) {
    Assert(elf_file != NULL, "elf_file does not exist");
    FILE* fp = fopen(elf_file, "rb");
    return fp;
}

Elf64_Ehdr read_elf_head(FILE* fp) {
    char headbuf[EI_NIDENT] = {0};
    rewind(fp);
    fread(headbuf, 1, EI_NIDENT, fp);

    // 判断文件类型是否为elf
    if (
        headbuf[0] != 0x7f && headbuf[1] != 0x45 &&
        headbuf[2] != 0x4c && headbuf[3] != 0x46) {
        panic("not a elf file!\n");
    }
    rewind(fp);
    Elf64_Ehdr elfHead64;
    fread(&elfHead64, sizeof(Elf64_Ehdr), 1, fp);
    rewind(fp);
    return elfHead64;
}

// 获得符号表列表
static size_t read_symbol_table(Elf64_Sym** symbol_table, Elf64_Shdr* section, FILE* fp) {
    rewind(fp);

    size_t sh_offset = section->sh_offset;
    fseek(fp, sh_offset, SEEK_SET);

    size_t sh_size = section->sh_size;        // 节大小
    size_t sh_entsize = section->sh_entsize;  // 有些节的表项是固定大小，比如符号表，这里表示符号表条目的大小
    if (sh_size % sh_entsize != 0) {
        // 说明没有整数个节
        return 0;
    }

    size_t entries = sh_size / sh_entsize;  // 条目个数

    *symbol_table = (Elf64_Sym*)malloc(sh_size);
    if (*symbol_table == NULL) {
        return 0;
    }

    size_t result = fread(*symbol_table, sh_entsize, entries, fp);
    rewind(fp);
    if (result != entries) {
        free(symbol_table);
        return 0;
    }
    return sh_size / sh_entsize;
}

static int read_string_table(const Elf64_Sym* elf64Sym, size_t n, Elf64_Shdr* elf64Sec, FILE* fp) {
    rewind(fp);

    size_t sh_offset = elf64Sec->sh_offset;
    fseek(fp, sh_offset, SEEK_SET);

    size_t index = 0;

    for (int i = 0; i < n; ++i) {
        if (ELF64_ST_TYPE(elf64Sym[i].st_info) != STT_FUNC) {
            continue;
        }
        elf_func[index].start = elf64Sym[i].st_value;
        elf_func[index].size = elf64Sym[i].st_size;
        size_t offset = elf64Sym[i].st_name;
        fseek(fp, sh_offset + offset, SEEK_SET);
        fscanf(fp, "%63s", elf_func[index].func_name);
        printf("offset = 0x%x, file offset = 0x%lx\n", elf64Sym[i].st_name, elf64Sym[i].st_name + sh_offset);
        index++;
    }
    rewind(fp);
    elf_func[index].func_name[0] = '\0';

    return 0;
}

void read_elf_section(FILE* fp, Elf64_Ehdr* elf_head) {
    size_t section_num = elf_head->e_shnum;
    size_t section_table_idx = elf_head->e_shoff;
    size_t section_string_table_idx = section_table_idx + elf_head->e_shentsize * elf_head->e_shstrndx;

    printf("section num : %lu", section_num);

    // 读取字符串表
    Elf64_Shdr string_table;
    fseek(fp, section_string_table_idx, SEEK_SET);
    fread(&string_table, sizeof(Elf64_Shdr), 1, fp);
    // 读取符号表
    Elf64_Shdr now_section_table;
    Elf64_Sym* symbol_table;
    size_t symbol_num = 0;
    fseek(fp, section_table_idx, SEEK_SET);
    for (int i = 0; i < section_num; i++) {
        fread(&now_section_table, sizeof(Elf64_Shdr), 1, fp);
        if (now_section_table.sh_type == SHT_SYMTAB) {
            symbol_num = read_symbol_table(&symbol_table, &now_section_table, fp);
            Assert(symbol_num != 0, "");
        }
    }

    rewind(fp);
    fseek(fp, section_table_idx, SEEK_SET);
    for (int i = 0; i < section_num; ++i) {
        fseek(fp, section_table_idx + i * sizeof(Elf64_Shdr), SEEK_SET);
        fread(&now_section_table, 1, sizeof(Elf64_Shdr), fp);
        if (now_section_table.sh_type == SHT_STRTAB) {
            if (i == elf_head->e_shstrndx) {
                // 为shstrtab
                continue;
            }
            rewind(fp);
            fseek(fp, string_table.sh_offset + now_section_table.sh_name + 1, SEEK_SET);
            char c;
            fread(&c, 1, 1, fp);
            if (c == 'd') {
                continue;
            }
            if (read_string_table(symbol_table, symbol_num, &now_section_table, fp)) {
                panic("");
            }
        }
    }

    rewind(fp);
}

FuncItem* find_func(paddr_t pc) {
    for (int i = 0; i < FUNC_NUM; i++) {
        if (pc >= elf_func[i].start && pc < (elf_func[i].start + elf_func[i].size)) {
            return &elf_func[i];
        }
    }
    return NULL;
}