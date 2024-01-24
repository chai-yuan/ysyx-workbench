.section bootloader, "ax"
.globl _loader
.type _loader, @function

_loader:
    la t0, _load_start       # 源地址
    la t1, _load_target      # 目标地址
    la t2, _load_target_end  # 目标结束地址

copy_loop:
    bleu t2, t1, copy_done  # 如果结束地址小于当前地址，完成复制
    lw a1, 0(t0)            # 加载4字节
    sw a1, 0(t1)            # 储存4字节
    addi t0, t0, 4          # 源地址加4
    addi t1, t1, 4          # 目标地址加4
    j copy_loop             # 跳转回循环开始

copy_done:
    # 跳转到psram开头
    call _start

