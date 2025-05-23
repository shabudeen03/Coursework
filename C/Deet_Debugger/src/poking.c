#include "utility.h"

void pokep(char* arg) {
	if(strlen(arg) == 0) {
		printf("?\n");
		fflush(stdout);
		log_error(arg - 5);
		return;
	}

	char* ptr = arg;
	while(*ptr != ' ') ptr++;
	if(*ptr == '\0') {
		printf("?\n");
		fflush(stdout);
		log_error(arg - 5);
		return;
	}

	while(*ptr == ' ') ptr++;

	if(*ptr == '\0') {
		printf("?\n");
		fflush(stdout);
		log_error(arg - 5);
		return;
	}

	int deetId, variableVal;
	unsigned long long addr;
	sscanf(arg, "%d %llx %d", &deetId, &addr, &variableVal);

	if(deetId < 0 || deetId >= numIdsCreated) {
		printf("?\n");
		fflush(stdout);
		log_error(arg - 5);
		return;
	}

	process* p = processes;
    while(p != NULL) {
    	if(p->deetId == deetId) {
    		break;
    	}

    	p = p->next;
    }

    if(ptrace(PTRACE_POKEDATA, p->pid, addr, variableVal) == -1) {
    	printf("?\n");
		fflush(stdout);
		log_error(arg - 5);
		return;
    }
}