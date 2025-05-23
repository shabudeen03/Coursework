#include "utility.h"

void helpMsg() {
	printf("Available commands:\nhelp -- Print this help message\nquit (<=0 args) -- Quit the program\nshow (<=1 args) -- Show process info\nrun (>=1 args) -- Start a process\nstop (1 args) -- Stop a running process\ncont (1 args) -- Continue a stopped process\nrelease (1 args) -- Stop tracing a process, allowing it to continue normally\nwait (1-2 args) -- Wait for a process to enter a specified state\nkill (1 args) -- Forcibly terminate a process\npeek (2-3 args) -- Read from the address space of a traced process\npoke (3 args) -- Write to the address space of a traced process\nbt (1-2 args) -- Show a stack trace for a traced process\n");
	fflush(stdout);
}