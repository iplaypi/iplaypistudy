#include<signal.h>
#include<stdio.h>
#include <unistd.h>

void handler(int signum) {
    // 处理函数只把接收到的信号编号打印出来
    if(signum == SIGIO)
        printf("SIGIO signal: %d\n", signum);
    else if(signum == SIGUSR1)
        printf("SIGUSR1 signal: %d\n", signum);
    else
        printf("error\n");
}

int main(void) {
    // 忽略 SIGINT,默认处理 SIGTERM,其它信号不注册都会导致程序退出
    signal(SIGIO, handler);
    signal(SIGUSR1, handler);
    signal(SIGINT, SIG_IGN);
    signal(SIGTERM, SIG_DFL);
    printf("SIGIO=%d,SIGUSR1=%d,SIGINT=%d,SIGTERM=%d\n", SIGIO, SIGUSR1, SIGINT, SIGTERM);
    // 以下是无限循环
    for(;;){
        sleep(10000);
    }
return 0;
}