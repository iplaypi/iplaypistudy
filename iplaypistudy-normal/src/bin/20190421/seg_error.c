#include <stdio.h>

int main(){
    char *str = "hello";
    // 非法赋值,想改变字符串内存地址的字符串值,不被允许
    *str = 'h'; 
    printf("%s\n", str);
    // 新定义字符串就可以
    char *str2 = "world"; 
    printf("%s\n", str2);
    return 0;
}