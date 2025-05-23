#include "utility.h"

void stopp(char* arg) {
	if(strlen(arg) == 0 || atoi(arg) >= numIdsCreated) {
		//error
		printf("?\n");
		fflush(stdout);
		log_error(arg - 5);
		return;
	}

	int deetId = atoi(arg);
	process* plist = processes;
	while(plist != NULL) {
		if(plist->deetId == deetId) {
			if(plist->state != PSTATE_RUNNING) {
				printf("?\n");
				fflush(stdout);
				log_error("stop");
				return;
			}

			break;
		}

		plist = plist->next;
	}

	log_state_change(plist->pid, plist->state, PSTATE_STOPPING, plist->status);
	plist->state = PSTATE_STOPPING;
	displayp(plist);
	kill(plist->pid, SIGSTOP);
}
