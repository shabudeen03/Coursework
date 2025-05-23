#include "utility.h"

void waitp(char* arg) {
	if(strlen(arg) == 0) {
		//error
		printf("?\n");
		fflush(stdout);
		log_error(arg - 5);
		return;
	}

	int hasSndArg = 1;
	int status = PSTATE_DEAD;
	char* ptr = arg;
	char* inp = arg;
	char* nullTerm;
	while(*ptr != '\0') {
		if(*ptr == ' ') {
			*ptr = '\0';
			nullTerm = ptr;
			ptr++;
			while(*ptr == ' ') ptr++;
			break;
		}

		ptr++;
	}

	if(strlen(ptr) == 0) hasSndArg = 0;

	int deetId = atoi(inp);
	if(deetId >= numIdsCreated || deetId < 0) {
		printf("?\n");
		fflush(stdout);
		*nullTerm = ' ';
		log_error(inp - 5);
		return;
	}

	process* p = processes;
	while(p != NULL) {
		if(p->deetId == deetId) {
			break;
		}

		p = p->next;
	}

	if(hasSndArg) {
		if(strcmp(ptr, "running") == 0) status = PSTATE_RUNNING;
		else if(strcmp(ptr, "stopping") == 0) status = PSTATE_STOPPING;
		else if(strcmp(ptr, "stopped") == 0) status = PSTATE_STOPPED;
		else if(strcmp(ptr, "continuing") == 0) status = PSTATE_CONTINUING;
		else if(strcmp(ptr, "killed") == 0) status = PSTATE_KILLED;
		else if(strcmp(ptr, "dead") != 0) {
			printf("?\n");
			fflush(stdout);
			log_error(inp - 5);
			return;
		}
	}

	if(p->state == PSTATE_DEAD && status < PSTATE_DEAD) {
		printf("?\n");
		fflush(stdout);
		log_error(inp - 5);
		return;
	}

	while(p->state != status);
}