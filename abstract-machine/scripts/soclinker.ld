ENTRY(_start)

PHDRS {
  text PT_LOAD;
  data PT_LOAD;
}

MEMORY {
  sram  : ORIGIN = 0x0f000000, LENGTH = 8K
  mrom  : ORIGIN = 0x20000000, LENGTH = 4K
  flash : ORIGIN = 0x30000000, LENGTH = 16M
  psram : ORIGIN = 0x80000000, LENGTH = 4M
}

SECTIONS {
  /* 加载到flash */
  . = ORIGIN(flash);
  .text : {
    *(entry)
    *(.text*)
  } > flash AT> flash : text
  .rodata : {
    *(.rodata*)
    *(.srodata*)
  } > flash AT> flash
  _etext = .;

  /* 加载到PSRAM */
  . = ORIGIN(psram);
  _bdata = .;
  .data : {
    *(.data*)
    *(.sdata*)
  } > psram AT> flash : data
  _edata = .;
  .bss (NOLOAD) : {
    *(.bss*)
    *(.sbss*)
    *(.scommon)
  } > psram

  /* 堆栈区 */
  _stack_top = ALIGN(0x1000);
  . = _stack_top + 0x4000;
  _stack_pointer = .;

  _heap_start = ALIGN(0x1000);
  _heap_end = ORIGIN(psram) + LENGTH(psram);
}

