#include <unistd.h>
#include <stdio.h>

int main()
{
  pid_t sid=getsid(0);          /* 会话 id */
  pid_t pgrp=getpgrp();         /* 进程组id */
  pid_t ppid=getppid();         /* 父进程id */
  pid_t pid=getpid();           /* 进程ID */
  printf("会话id:%d\n进程组id:%d\n父进程id:%d\n进程id:%d\n",sid,pgrp,ppid,pid);
}
