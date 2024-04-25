#ifndef CONFIG_H_
#define CONFIG_H_

#define MEM_BASE 0x30000000 // 加载bin文件的位置
#define CONFIG_PC 0x30000000

// 顶层文件选择
#define CONFIG_SOC 1
// #define CONFIG_NPC 1

// 调试设置
// #define CONFIG_DIFFTEST 1
// #define CONFIG_NVBOARD 1 // 仅限Soc
// #define CONFIG_VTRACE 1
// #define CONFIG_LOOP_CHECK 1

#endif
