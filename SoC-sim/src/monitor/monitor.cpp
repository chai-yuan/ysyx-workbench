#include <common.h>
#include <memory/paddr.h>

void init_log(const char* log_file);
void init_mem();
void init_difftest(char* ref_so_file, long img_size, int port);

static void welcome() {
    Log("Build time: %s, %s", __TIME__, __DATE__);
}

#include <getopt.h>

void sdb_set_batch_mode();

static char* log_file = NULL;
static char* diff_so_file = NULL;
static char* img_file = NULL;
static int difftest_port = 1234;

static long load_img() {
    if (img_file == NULL) {
        Log("No image is given. Use the default build-in image.");
        return 4096;  // built-in image size
    }

    FILE* fp = fopen(img_file, "rb");
    Assert(fp, "Can not open '%s'", img_file);

    fseek(fp, 0, SEEK_END);
    long size = ftell(fp);

    Log("The image is %s, size = %ld", img_file, size);
    Log("Load image to 0x%x", mem_base);

    fseek(fp, 0, SEEK_SET);
    int ret = fread(guest_to_host(mem_base), size, 1, fp);
    assert(ret == 1);

    fclose(fp);
    return size;
}

static int parse_args(int argc, char* argv[]) {
    const struct option table[] = {
        {"batch", no_argument, NULL, 'b'},
        {"log", required_argument, NULL, 'l'},
        {"diff", required_argument, NULL, 'd'},
        {"port", required_argument, NULL, 'p'},
        {"help", no_argument, NULL, 'h'},
        {0, 0, NULL, 0},
    };
    int o;
    while ((o = getopt_long(argc, argv, "-bhl:d:p:", table, NULL)) != -1) {
        switch (o) {
            case 'b':
                sdb_set_batch_mode();
                break;
            case 'p':
                sscanf(optarg, "%d", &difftest_port);
                break;
            case 'l':
                log_file = optarg;
                break;
            case 'd':
                diff_so_file = optarg;
                break;
            case 1:
                img_file = optarg;
                return 0;
            default:
                printf("Usage: %s [OPTION...] IMAGE [args]\n\n", argv[0]);
                printf("\t-b,--batch              run with batch mode\n");
                printf("\t-l,--log=FILE           output log to FILE\n");
                printf(
                    "\t-d,--diff=REF_SO        run DiffTest with reference "
                    "REF_SO\n");
                printf(
                    "\t-p,--port=PORT          run DiffTest with port PORT\n");
                printf("\n");
                exit(0);
        }
    }
    return 0;
}

void init_monitor(int argc, char* argv[]) {
    /* Parse arguments. */
    parse_args(argc, argv);

    /* Open the log file. */
    init_log(log_file);

    /* Initialize memory. */
    init_mem();

    /* Load the image to memory. This will overwrite the built-in image. */
    long img_size = load_img();

    /* Initialize differential testing. */
    IFDEF(CONFIG_DIFFTEST,
          init_difftest(diff_so_file, img_size, difftest_port));

    /* Display welcome message. */
    welcome();
}
