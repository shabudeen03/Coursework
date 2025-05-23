#include "utility.h"

volatile sig_atomic_t terminate = 0;

void killp(char* arg) {
	numProcessesInUse--;
	int deetId = atoi(arg);
	if((arg[0] != '0' && deetId == 0) || deetId >= numIdsCreated) {
		//error
		printf("?\n");
		fflush(stdout);
		log_error(arg - 5);
		return;
	}

	process* target = processes;
	while(target != NULL) {
		if(target->deetId == deetId) {
			if(target->state != PSTATE_NONE && target->state != PSTATE_KILLED && target->state != PSTATE_DEAD) {
				log_state_change(target->pid, target->state, PSTATE_KILLED, target->status);
				target->state = PSTATE_KILLED;
				displayp(target);
				kill(target->pid, SIGKILL);
				waitpid(target->pid, &target->status, 0);
			}

			break;
		}

		target = target->next;
	}

	if(target == NULL) {
		printf("?\n");
		fflush(stdout);
		log_error(arg - 5);
		return;
	}

	target->state = PSTATE_DEAD;
	displayp(target);
	terminate = 1;
}