#include "utility.h"

volatile sig_atomic_t childrenDead = 0;
volatile sig_atomic_t done = 0;
volatile sig_atomic_t promptPrint = 0;
char* line = NULL;

void sigint_handler(int sig) {
	log_signal(sig);
	kill(getpid(), SIGCHLD);
	quitDeet();

	if(line != NULL) free(line);
	log_shutdown();
	exit(0);
}

void sigchld_handler(int sig) {
	log_signal(sig);
	sigset_t mask;
	sigfillset(&mask);
	int olderrno = errno;
	int status;
	pid_t pid;

	while((pid = waitpid(-1, &status, WNOHANG | WUNTRACED | WCONTINUED)) > 0) {
		process* proc = findp(pid);
		if(proc == NULL) continue;
		proc->status = status;

		if(WIFSTOPPED(status)) {
			log_state_change(pid, proc->state, PSTATE_STOPPED, proc->status);

			sigprocmask(SIG_BLOCK, &mask, NULL);
			proc->state = PSTATE_STOPPED;
			sigprocmask(SIG_UNBLOCK, &mask, NULL);

			displayp(proc);
		} else if(WIFSIGNALED(status) && WTERMSIG(status) == SIGKILL) {
			log_state_change(pid, PSTATE_KILLED, PSTATE_DEAD, proc->status);

			sigprocmask(SIG_BLOCK, &mask, NULL);
			numProcessesInUse--;
			sigprocmask(SIG_UNBLOCK, &mask, NULL);
		} else if(WIFEXITED(status)) {
			log_state_change(pid, PSTATE_RUNNING, PSTATE_DEAD, proc->status);

			sigprocmask(SIG_BLOCK, &mask, NULL);
			proc->state = PSTATE_DEAD;
			numProcessesInUse--;
			sigprocmask(SIG_UNBLOCK, &mask, NULL);
			displayp(proc);

		}
		// else if(WIFCONTINUED(status)) {
		// 	log_state_change(pid, PSTATE_STOPPED, PSTATE_RUNNING, proc->status);

		// 	sigprocmask(SIG_BLOCK, &mask, NULL);
		// 	proc->state = PSTATE_RUNNING;
		// 	sigprocmask(SIG_UNBLOCK, &mask, NULL);
		// }
	}

	promptPrint = 1;
	if(numProcessesInUse == 0) childrenDead = 1;
	errno = olderrno;
}