#include "utility.h"

void showp(char* arg) {
	if(numIdsCreated == 0 || atoi(arg) >= numIdsCreated) {
		log_error(arg - 5);
		printf("?\n");
		fflush(stdout);
		return;
	}

	process* plist = processes;
	if(strlen(arg) == 0) { //display all processes
		while(plist != NULL) {
			if(plist->state != PSTATE_NONE) {
				displayp(plist);
			}

			plist = plist->next;
		}
	} else {
		while(plist != NULL) {
			if(plist->state != PSTATE_NONE && plist->deetId == atoi(arg)) {
				displayp(plist);
				break;
			}

			plist = plist->next;
		}
	}
}