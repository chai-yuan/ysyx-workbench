.section bootloader, "ax"
.globl _start
.type _start, @function

_start:
    la t0, _load_start       # 源地址
    la t1, _load_target      # 目标地址
    la t2, _load_len         # 目标长度

copy_loop:
    blez t2, copy_done      # 长度小于等于0,拷贝完毕
    lw a1, 0(t0)            # 加载4字节
    sw a1, 0(t1)            # 储存4字节
    addi t0, t0, 4          # 源地址加4
    addi t1, t1, 4          # 目标地址加4
    addi t2, t2, -4         # 长度减4
    j copy_loop             # 跳转回循环开始

copy_done:
    # 跳转到load位置
    call _load_target
