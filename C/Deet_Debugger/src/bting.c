#include "utility.h"

void btp(char* arg) {
	if(strlen(arg) == 0) {
		printf("?\n");
		fflush(stdout);
		log_error(arg - 3);
		return;
	}

	if(*arg < '0' || *arg > '9') {
		printf("?\n");
		fflush(stdout);
		log_error(arg - 3);
		return;
	}

	char* ptr = arg;
	int hasSecond = 1;
	int stackLen = 10;
	while(*ptr != ' ' || *ptr != '\0') ptr++;

	if(*ptr == '\0') {
		hasSecond = 0;
	}

	int deetId;
	if(hasSecond) {
		sscanf(arg, "%d %d", &deetId, &stackLen);
	} else {
		deetId = atoi(arg);
	}

	if(deetId < 0 || deetId >= numIdsCreated || stackLen < 1) {
		printf("?\n");
		fflush(stdout);
		log_error(arg - 3);
		return;
	}

	process* p = processes;
	while(p != NULL) {
		if(p->deetId == deetId) {
			break;
		}

		p = p->next;
	}

	if(p->isTraced == 0) {
		printf("?\n");
		fflush(stdout);
		log_error(arg - 3);
		return;
	}

	//incomplete
	return;
}