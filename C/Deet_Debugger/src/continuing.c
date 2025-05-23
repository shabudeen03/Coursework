#include "utility.h"

void contp(char* arg) {
	if(strlen(arg) == 0 || atoi(arg) >= numIdsCreated) {
		//error
		printf("?\n");
		fflush(stdout);
		log_error(arg - 5);
		return;
	}

	//error
	if(atoi(arg) >= numIdsCreated) {
		printf("?\n");
		fflush(stdout);
		log_error(arg - 5);
		return;
	}

	process* plist = processes;
	while(plist != NULL) {
		if(plist->deetId == atoi(arg)) { //process found
			if(plist->state == PSTATE_STOPPED) {
				break;
			} else {
				printf("?\n");
				fflush(stdout);
				log_error(arg - 5);
				return;
			}
		}

		plist = plist->next;
	}

	plist->state = PSTATE_CONTINUING;

	log_state_change(plist->pid, PSTATE_STOPPED, PSTATE_RUNNING, plist->status);

	if(plist->isTraced == 0) {
		kill(plist->pid, SIGCONT);
	} else {
		ptrace(PTRACE_CONT, plist->pid, NULL, NULL);
	}

	plist->state = PSTATE_RUNNING;
	displayp(plist);
}