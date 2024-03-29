ENTRY(_start)

PHDRS { text PT_LOAD; data PT_LOAD; }

MEMORY {
  sdram (rwx) : ORIGIN = 0x80000000, LENGTH = 128M
}

SECTIONS {
  . = ORIGIN(sdram);

  .text : {
    *(entry)
    *(.text*)
  } : text
  .rodata : {
    *(.rodata*)
  }
  .data : {
    *(.data*)
  } : data
  .bss : {
    *(.bss*)
    *(.sbss*)
    *(.scommon)
  }

  PROVIDE (_stack_size = 0x8000);

  .stack_heap : {
    _stack_top = ALIGN(0x1000);
    . = _stack_top + _stack_size;
    _stack_pointer = .;

    _heap_start = ALIGN(0x1000);
    _heap_end = ORIGIN(sdram) + LENGTH(sdram);
  }
}

