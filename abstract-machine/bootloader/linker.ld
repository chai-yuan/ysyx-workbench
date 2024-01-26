ENTRY(_start)

MEMORY {
  flash (rx) : ORIGIN = 0x30000000, LENGTH = 16M
}

SECTIONS {
  . = ORIGIN(flash);

  .text : {
    *(bootloader)
    _load_start = .;
  }
}

