#include "utility.h"

extern volatile sig_atomic_t childrenDead;

void quitDeet() {
	if(numIdsCreated == 0) return;

	process* plist = processes;
	while(plist != NULL) {
		if(plist->state != PSTATE_NONE && plist->state != PSTATE_DEAD && plist->state != PSTATE_KILLED) {
			log_state_change(plist->pid, plist->state, PSTATE_KILLED, plist->status);
			plist->state = PSTATE_KILLED;
		}

		plist = plist->next;
	}

	plist = processes;
	while(plist != NULL) {
		if(plist->state == PSTATE_KILLED) {
			displayp(plist);
		}

		plist = plist->next;;
	}

	plist = processes;
	while(plist != NULL) {
		if(plist->state != PSTATE_NONE && plist->state != PSTATE_DEAD) {
			kill(plist->pid, SIGKILL);
		}

		plist = plist->next;
	}

	while(!childrenDead);

	plist = processes;
	process* next;
	while(plist != NULL) {
		if(plist->state == PSTATE_KILLED) {
			plist->state = PSTATE_DEAD;
			displayp(plist);
		}

		next = plist->next;
		free(plist);
		plist = next;
	}

	done = 1;
}