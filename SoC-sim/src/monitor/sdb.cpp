#include <common.h>
#include <cpu/cpu.h>
#include <cpu/reg.h>
#include <memory/paddr.h>
#include <readline/history.h>
#include <readline/readline.h>
#include <utils.h>

static int is_batch_mode = false;

/* We use the `readline' library to provide more flexibility to read from stdin. */
static char* rl_gets() {
    static char* line_read = NULL;

    if (line_read) {
        free(line_read);
        line_read = NULL;
    }

    line_read = readline("(npc) ");

    if (line_read && *line_read) {
        add_history(line_read);
    }

    return line_read;
}

static int cmd_c(char* args) {
    cpu_exec(-1);
    return 0;
}

static int cmd_q(char* args) {
    npc_state.state = NPC_QUIT;
    return -1;
}

static int cmd_help(char* args);

static int cmd_si(char* args) {
    if (args == NULL) {
        cpu_exec(1);
    } else {
        cpu_exec(atoi(args));
    }
    return 0;
}

static int cmd_info(char* args) {
    Assert(args != NULL, "The info command requires an argument");
    if (*args == 'r') {
        isa_reg_display();
    } else {
        panic("The info command received incorrect parameters");
    }
    return 0;
}

static int cmd_x(char* args) {
    char* slen = strtok(NULL, " ");
    char* saddr = strtok(NULL, " ");
    int len = 0;
    vaddr_t addr = 0;

    sscanf(slen, "%d", &len);
    sscanf(saddr, "%x", &addr);

    Log("read len %d,addr %x",len,addr);

    for (int i = 0; i < len; i++) {
        word_t rdata;
        pmem_read(addr, &rdata);
        printf("0x%08x: %08x\n", addr, rdata);
        addr += 4;
    }
    return 0;
}

static struct {
    const char* name;
    const char* description;
    int (*handler)(char*);
} cmd_table[] = {
    {"help", "Display information about all supported commands", cmd_help},
    {"c", "Continue the execution of the program", cmd_c},
    {"q", "Exit NEMU", cmd_q},
    {"si", "Execute N steps, pause after N instructions", cmd_si},
    {"info", "Print the program status", cmd_info},
    {"x", "Scan memory", cmd_x},
};

#define NR_CMD ARRLEN(cmd_table)

static int cmd_help(char* args) {
    /* extract the first argument */
    char* arg = strtok(NULL, " ");
    int i;

    if (arg == NULL) {
        /* no argument given */
        for (i = 0; i < NR_CMD; i++) {
            printf("%s - %s\n", cmd_table[i].name, cmd_table[i].description);
        }
    } else {
        for (i = 0; i < NR_CMD; i++) {
            if (strcmp(arg, cmd_table[i].name) == 0) {
                printf("%s - %s\n", cmd_table[i].name, cmd_table[i].description);
                return 0;
            }
        }
        printf("Unknown command '%s'\n", arg);
    }
    return 0;
}

void sdb_set_batch_mode() {
    Log("batch mode on");
    is_batch_mode = true;
}

void sdb_mainloop() {
    if (is_batch_mode) {
        cmd_c(NULL);
        return;
    }

    for (char* str; (str = rl_gets()) != NULL;) {
        char* str_end = str + strlen(str);

        /* extract the first token as the command */
        char* cmd = strtok(str, " ");
        if (cmd == NULL) {
            continue;
        }

        /* treat the remaining string as the arguments,
         * which may need further parsing
         */
        char* args = cmd + strlen(cmd) + 1;
        if (args >= str_end) {
            args = NULL;
        }

        int i;
        for (i = 0; i < NR_CMD; i++) {
            if (strcmp(cmd, cmd_table[i].name) == 0) {
                if (cmd_table[i].handler(args) < 0) {
                    return;
                }
                break;
            }
        }

        if (i == NR_CMD) {
            printf("Unknown command '%s'\n", cmd);
        }
    }
}
