calculator: main.c Makefile
	riscv64-unknown-elf-gcc					\
	-O3										\
	-m32									\
	-static									\
	-nostdlib								\
	-ffreestanding							\
	-fvisibility=hidden						\
	-Wa,-march=RVIMAFDXhwacha				\
	-T../../hw/src/misc/turborav.ld	\
	-I../lib/								\
	$<										\
	-o $@
