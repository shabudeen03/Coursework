#include "utility.h"

void peekp(char* arg) {
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

	while(*ptr != ' ' && *ptr != '\0') ptr++;

	int hasThird = 1;
	if(*ptr == '\0') hasThird = 0;

	//not going to bother with whitespace errors etc
	int deetId, numRead;
    unsigned long long addr;
    if(hasThird) sscanf(arg, "%d %llx %d", &deetId, &addr, &numRead);
    else {
    	numRead = 1;
    	sscanf(arg, "%d %llx", &deetId, &addr);
    }

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

    long value;
    while(numRead > 0) {
		value = ptrace(PTRACE_PEEKDATA, p->pid, addr, NULL);
    	if(value == -1) {
    		printf("?\n");
    		fflush(stdout);
    		log_error(arg - 5);
	    	return;
    	}

    	printf("%016llx\t%016lx\n", addr, value);
    	numRead--;
    }
}