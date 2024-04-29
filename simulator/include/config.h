#ifndef CONFIG_H_
#define CONFIG_H_

#ifdef ysyxSoCFull

#define MEM_BASE 0x30000000
#define CONFIG_PC 0x30000000 // 加载bin文件的位置

#elif CRRVTop

#define MEM_BASE 0x80000000
#define CONFIG_PC 0x80000000 // 加载bin文件的位置

#endif

// 调试设置
#define CONFIG_DIFFTEST 1
// #define CONFIG_NVBOARD 1 // 仅限Soc
#define CONFIG_VTRACE 1
// #define CONFIG_LOOP_CHECK 1

#endif
