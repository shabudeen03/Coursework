#include "utility.h"

void releasep(char* arg) {
	if(strlen(arg) == 0 || atoi(arg) >= numIdsCreated) {
		//error
		printf("?\n");
		fflush(stdout);
		log_error(arg - 8);
		return;
	}

	int deetId = atoi(arg);
	process* p = processes;
	while(p != NULL) {
		if(p->deetId == deetId) {
			if(p->isTraced == 0) {
				printf("?\n");
				fflush(stdout);
				log_error(arg - 8);
				return;
			}

			p->isTraced = 0;
			break;
		}

		p = p->next;
	}

	ptrace(PTRACE_DETACH, p->pid, NULL, NULL);

	log_state_change(p->pid, PSTATE_STOPPED, PSTATE_RUNNING, p->status);
	p->state = PSTATE_RUNNING;
	displayp(p);
}