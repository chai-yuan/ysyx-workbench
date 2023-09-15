#include <common.h>

static Context* do_event(Event e, Context* c) {
    switch (e.event) {
        case EVENT_YIELD:
            c->mepc = c->mepc + 4;
            printf("EVENT_YIELD\n");
            break;
        default:
            panic("Unhandled event ID = %d", e.event);
    }

    return c;
}

void init_irq(void) {
    Log("Initializing interrupt/exception handler...");
    cte_init(do_event);
}