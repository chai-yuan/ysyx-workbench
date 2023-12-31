## CRRV 4 CPU

| 项                    | 数值           |
| --------------------- | -------------- |
| 指令集支持            | riscv32IM_Zicsr |
| 处理器结构            | 5级流水线      |
| IPC(microbench/train) | ?      |

任务计划
- [x] 通过cpu test
- [x] 通过yield和yield-os测试
- [x] 运行rt-thread
- [x] 支持AXI lite总线 和 随机延迟
- [x] 仲裁器
- [x] 虚拟设备(串口和时钟)
- [ ] 支持cache
- [ ] 支持TLB MMU
