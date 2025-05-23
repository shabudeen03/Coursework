#include <signal.h>
#include <sys/ptrace.h>
#include <fcntl.h>
#include "deet.h"
#include "csapp.h"

typedef struct process {
	int deetId; //deet id
	pid_t pid; //child process id
	int state; //none, running, stopped, etc
	int isTraced:1; //if traced or not
	int status; //status of process
	struct process* next;
	char program[];
} process;

extern volatile sig_atomic_t done;
extern volatile sig_atomic_t terminate;
extern char* prompt;
extern int printDeet;
extern int numProcessesInUse; //Initially 0
extern int numIdsCreated; //Initially 0
extern process* processes;
extern process* lastProcess;
void displayp(process* proc);
process* findp(pid_t pid);
ssize_t sio_puts(char s[]);
ssize_t sio_putl(long v);
void sigint_handler(int sig);
void sigchld_handler(int sig);
process* findp(pid_t pid);
void displayp(process* proc);
void runp(char* cmd);
void sigint_handler(int sig);
void sigchld_handler(int sig);

void quitDeet();
void helpMsg();
void showp();
void runp();
void stopp();
void contp();
void releasep();
void waitp();
void killp();
void peekp();
void pokep();
void btp();