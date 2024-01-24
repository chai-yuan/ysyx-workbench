ENTRY(_loader)

MEMORY {
  flash (rx)  : ORIGIN = 0x30000000, LENGTH = 16M
  psram (rwx) : ORIGIN = 0x80000000, LENGTH = 4M
}

REGION_ALIAS("REGION_BOOTLOAD", flash);
REGION_ALIAS("REGION_TEXT", psram);
REGION_ALIAS("REGION_DATA", psram);
REGION_ALIAS("REGION_STKHP", psram);

SECTIONS {
  .bootloader :
  {
    *(bootloader)
    _bootloader_end = .;
  } > REGION_BOOTLOAD

  .text :
  AT (_bootloader_end)
  {
    _text_start = .;
    *(entry)
    *(.text*)
  } > REGION_TEXT
  _text_size = SIZEOF(.text);

  .data :
  AT (_bootloader_end + _text_size)
  {
    _data_start = .;
    *(.rodata*)
    *(.srodata*)
    *(.data*)
    *(.sdata*)
    _data_end = .;
  } > REGION_DATA
  _data_size = SIZEOF(.data);

  _load_start = LOADADDR(.text);
  _load_target = _text_start;
  _load_target_end = _bss_start;

  .bss (NOLOAD) :
  {
    _bss_start = .;
    *(.bss*)
    *(.sbss*)
    *(.scommon)
  } > REGION_STKHP

  PROVIDE (_stack_size = 0x4000);

  .stack_heap (NOLOAD) :
  {
    _stack_top = ALIGN(0x1000);
    . = _stack_top + _stack_size;
    _stack_pointer = .;

    _heap_start = ALIGN(0x1000);
    _heap_end = ORIGIN(REGION_STKHP) + LENGTH(REGION_STKHP);
  } > REGION_STKHP
}

