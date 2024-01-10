_mrom_start = 0x20000000;
_mrom_end   = 0x20000fff;
_sram_start = 0x0f000000;
_sram_end   = 0x0fffffff;

_pmem_start = _mrom_start;
_stack_top  = _sram_start;
_stack_size = 0x800;
_heap_end   = _sram_end;

ENTRY(_start)
PHDRS { text PT_LOAD; data PT_LOAD; }

SECTIONS {
  . = _pmem_start;
  .text : {
    *(entry)
    *(.text*)
  } : text
  etext = .;
  _etext = .;
  .rodata : {
    *(.rodata*)
  }
  .data : {
    *(.data)
  } : data
  edata = .;
  _data = .;
  .bss : {
	_bss_start = .;
    *(.bss*)
    *(.sbss*)
    *(.scommon)
  }
  . = _stack_top + _stack_size;
  _stack_pointer = .;
  end = .;
  _end = .;
  _heap_start = ALIGN(0x1000);
}

