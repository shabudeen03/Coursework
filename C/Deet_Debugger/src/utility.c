#include "utility.h"

extern volatile sig_atomic_t childTerm;
process* processes = NULL;
process* lastProcess = NULL;
int numProcessesInUse = 0; //Initially 0
int numIdsCreated = 0; //Initially 0

process* findp(pid_t pid) {
	process* target = processes;
	while(target != NULL) {
		// printf("target pid - %d\n", target->pid);
		if(target->pid == pid) break;
		target = target->next;
	}

	return target;
}

//too lazy to use now
process* searchP(int did) {
	process* target = processes;
	while(target != NULL) {
		// printf("target pid - %d\n", target->pid);
		if(target->deetId == did) break;
		target = target->next;
	}

	return target;
}

void displayp(process* proc) {
	sigset_t mask;
	sigfillset(&mask);
	sigprocmask(SIG_BLOCK, &mask, NULL);

	printf("%d\t%d\t", proc->deetId, proc->pid);

	if(proc->isTraced) {
		printf("T\t");
	} else {
		printf("U\t");
	}

	char* status;
	int* exitPtr = NULL;
	int exitCode = 0;
	if(proc->state == PSTATE_RUNNING) {
		status = "running";
	} else if(proc->state == PSTATE_STOPPING) {
		status = "stopping";
	} else if(proc->state == PSTATE_STOPPED) {
		status = "stopped";
	} else if(proc->state == PSTATE_CONTINUING) {
		status = "continuing";
	} else if(proc->state == PSTATE_KILLED) {
		status = "killed";
	} else if(proc->state == PSTATE_DEAD) {
		status = "dead";
		exitPtr = &exitCode;
		exitCode = proc->status;
	}

	printf("%s\t", status);

	if(exitPtr != NULL) {
		printf("0x%x", exitCode);
	}

	printf("\t%s\n", proc->program);
	sigprocmask(SIG_UNBLOCK, &mask, NULL);
}