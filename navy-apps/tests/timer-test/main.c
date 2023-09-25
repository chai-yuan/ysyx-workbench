#include <stdio.h>
#include <assert.h>
#include <time.h>
#include <NDL.h>

int main() {
    	NDL_Init(0);
    	int ms = 0;
	
    	while(1) {
		while (NDL_GetTicks() < ms) {
			for(int i=0;i<100000;i++);
		}
		
		printf("Current Time: %ldms\n", NDL_GetTicks());
		ms += 500;
    	}
	NDL_Quit();
	return 0;
}
