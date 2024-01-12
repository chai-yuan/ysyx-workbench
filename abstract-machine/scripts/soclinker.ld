ENTRY(_start)

PHDRS {
  text PT_LOAD;
  data PT_LOAD;
}

MEMORY {
  mrom : ORIGIN = 0x20000000, LENGTH = 4K
  sram : ORIGIN = 0x0f000000, LENGTH = 8K
}

SECTIONS {
  /* 加载到MROM */
  . = ORIGIN(mrom);
  .text : {
    *(entry)
    *(.text*)
  } > mrom AT> mrom : text
  .rodata : {
    *(.rodata*)
    *(.srodata*)
  } > mrom AT> mrom
  _etext = .;

  /* 加载到SRAM */
  . = ORIGIN(sram);
  _bdata = .;
  .data : {
    *(.data*)
    *(.sdata*)
  } > sram AT> mrom : data
  _edata = .;
  .bss (NOLOAD) : {
    *(.bss*)
    *(.sbss*)
    *(.scommon)
  } > sram

  /* 堆栈区 */
  _heap_start = ALIGN(16);
  _heap_end = ORIGIN(sram) + LENGTH(sram) - 0x200;

  _stack_top = ORIGIN(sram) + LENGTH(sram);
  . = _stack_top;
  _stack_pointer = .;
}

