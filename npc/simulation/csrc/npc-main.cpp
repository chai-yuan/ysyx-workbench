#include <common.h>
#include <monitor.h>

NPCState npc_state = {.state = NPC_STOP, .halt_pc = 0, .halt_ret = 0};

int is_exit_status_bad() {
    int good = (npc_state.state == NPC_END && npc_state.halt_ret == 0) ||
               (npc_state.state == NPC_QUIT);
    return !good;
}

int main(int argc, char* argv[]) {
    init_monitor(argc, argv);

    sdb_mainloop();

    return is_exit_status_bad();
}