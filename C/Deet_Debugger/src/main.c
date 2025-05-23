#include "utility.h"

extern volatile sig_atomic_t done;
int printDeet = 1;
char* prompt = "deet> ";
extern volatile sig_atomic_t terminate;
extern volatile sig_atomic_t promptPrint;
extern char* line;

int main(int argc, char *argv[]) {
    log_startup();

    //install sigint handler
    struct sigaction sigint_action;
    sigint_action.sa_handler = sigint_handler;
    sigemptyset(&sigint_action.sa_mask);
    sigint_action.sa_flags = SA_RESTART;

    if(sigaction(SIGINT, &sigint_action, NULL) < 0) {
        unix_error("Signal error");
    }

    struct sigaction sigchld_action;
    sigchld_action.sa_handler = sigchld_handler;
    sigemptyset(&sigchld_action.sa_mask);
    sigaddset(&sigchld_action.sa_mask, SIGCHLD);
    sigchld_action.sa_flags = SA_RESTART;

    if(sigaction(SIGCHLD, &sigchld_action, NULL) < 0) {
        unix_error("Signal error");
    }

    if(argc >= 2 && strcmp(argv[1], "-p") == 0) {
        printDeet = 0;
    }

    size_t len = 0;
    ssize_t nread;

    if(printDeet == 0) {
        prompt = "";
    }

    log_prompt();
    printf("%s", prompt);
    fflush(stdout);
    while(!done) {
        if((nread = getline(&line, &len, stdin)) == -1) {
            if(errno != EINTR) {
                log_shutdown();
                break;
            } else {
                continue;
            }
        }

        log_input(line);
        if(line[nread - 1] == '\n') {
            line[nread - 1] = '\0';
        }

        if(strcmp(line, "quit") == 0) {
            terminate = 1;
            quitDeet();
            break;
        } else if(strcmp(line, "help") == 0) {
            helpMsg();
        } else if(strncmp(line, "show", 4) == 0) {
            char* ptr = line + 4;
            while(*ptr == ' ') {
                ptr++;
            }

            showp(ptr);
        } else if(strncmp(line, "run", 3) == 0) {
            char* ptr = line + 4;
            while(*ptr == ' ') {
                ptr++;
            }

            if(strlen(ptr) < 1) {
                printf("?\n");
                fflush(stdout);
                log_error(line);
            } else {
                runp(ptr);
                while(!promptPrint);
                promptPrint = 0;
            }
        } else if(strncmp(line, "stop", 4) == 0) {
            char* ptr = line + 5;
            while(*ptr == ' ') {
                ptr++;
            }

            stopp(ptr);
        } else if(strncmp(line, "cont", 4) == 0) {
            char* ptr = line + 5;
            while(*ptr == ' ') {
                ptr++;
            }

            contp(ptr);
        } else if(strncmp(line, "release", 7) == 0) {
            char* ptr = line + 8;
            while(*ptr == ' ') {
                ptr++;
            }

            releasep(ptr);
        } else if(strncmp(line, "wait", 4) == 0) {
            char* ptr = line + 5;
            if(strlen(ptr) < 1) {
                printf("?\n");
                fflush(stdout);
                log_error(line);
            }

            while(*ptr == ' ') {
                ptr++;
            }

            waitp(ptr);
        } else if(strncmp(line, "kill", 4) == 0) {
            char* ptr = line + 5;
            while(*ptr == ' ') {
                ptr++;
            }

            killp(ptr);
            while(!terminate);
            terminate = 0;
        } else if(strncmp(line, "peek", 4) == 0) {
            char* ptr = line + 5;
            while(*ptr == ' ') {
                ptr++;
            }

            peekp(ptr);
        } else if(strncmp(line, "poke", 4) == 0) {
            char* ptr = line + 5;
            while(*ptr == ' ') {
                ptr++;
            }

            pokep(ptr);
        } else if(strncmp(line, "bt", 2) == 0) {
            char* ptr = line + 3;
            while(*ptr == ' ') {
                ptr++;
            }

            btp(ptr);
        } else { //Command Not defined
            printf("?\n");
            fflush(stdout);
            log_error(line);
        }

        //At the end before next iteration
        log_prompt();
        printf("%s", prompt);
        fflush(stdout);
    }

    free(line);
    log_shutdown();
    return 0;
}