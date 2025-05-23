#include "utility.h"

void runp(char* cmd) {
	int numArgs = 1;
	char* ptr = cmd;
	while(*ptr != '\0') {
		if(*ptr == ' ') {
			numArgs++;
		}

		ptr++;
	}

	//Find deet id of the process and put in the list
	int deetId = -1;
	numProcessesInUse++;
	process* proc = NULL;

	if(numProcessesInUse > numIdsCreated) { //insufficient ids, create new one
		proc = malloc(sizeof(process) + strlen(cmd) + 1);
		strcpy(proc->program, cmd);
		proc->next = NULL;
		proc->state = PSTATE_NONE;
		if(lastProcess != NULL) lastProcess->next = proc;
		if(processes == NULL) {
			processes = proc;
		}

		lastProcess = proc;
		deetId = numIdsCreated++;
	} else {
		process* prev = NULL;
		process* plist = processes;
		while(plist != NULL) {
			if(plist->state == PSTATE_NONE || plist->state == PSTATE_DEAD) {
				proc = plist;
				if(proc->state == PSTATE_DEAD) {
					proc->status = 0;
					log_state_change(proc->pid, proc->state, PSTATE_NONE, proc->status);
					proc->state = PSTATE_NONE;
					deetId = proc->deetId;
				}

				proc = plist;
				break;
			}

			prev = plist;
			plist = plist->next;
		}

		if(strlen(proc->program) < strlen(cmd)) {
			process* nextP = proc->next;
			proc = realloc(proc, sizeof(process) + strlen(cmd) + 1);
			strcpy(proc->program, cmd);
			proc->next = nextP;
			if(prev != NULL) prev->next = proc;
			else processes = proc;
		}
	}

	//Set up process
	proc->deetId = deetId;
	proc->isTraced = 1;
	proc->status = 0;
	size_t size = (numArgs + 1) * 8;
	char** arguments = malloc(size);

	char* cmnd = malloc(strlen(cmd) + 1);
	strcpy(cmnd, cmd);

	int ctr = 0;
	const char* delim = " ";
	char* token = strtok(cmnd, delim);
	while(token != NULL && ctr < numArgs) {
		arguments[ctr] = malloc(strlen(token) + 1);
		strcpy(arguments[ctr], token);
		ctr++;
		token = strtok(NULL, delim);
	}

	free(cmnd);
	arguments[ctr] = NULL;

	sigset_t mask;
	sigfillset(&mask);
	// sigaddset(&mask, SIGCHLD);

	pid_t pid; //handle fork error
	if((pid = fork()) == 0) { //child process
		close(STDOUT_FILENO);
		if(dup2(STDERR_FILENO, STDOUT_FILENO) == -1) {
			unix_error("dup2 failed");
			exit(EXIT_FAILURE);//change it
		}

		if (ptrace(PTRACE_TRACEME, 0, NULL, NULL) == -1) {
			unix_error("ptrace failed");
			exit(EXIT_FAILURE);
		}

		execvp(arguments[0], arguments);
		exit(0x100);
	} else {
		sigprocmask(SIG_BLOCK, &mask, NULL);

		log_state_change(pid, proc->state, PSTATE_RUNNING, proc->status);
		proc->pid = pid;
		proc->state = PSTATE_RUNNING;
		displayp(proc);

		for(int i=0; i<ctr; i++) {
			free(arguments[i]);
		}

		free(arguments);
		sigprocmask(SIG_UNBLOCK, &mask, NULL);
	}
}