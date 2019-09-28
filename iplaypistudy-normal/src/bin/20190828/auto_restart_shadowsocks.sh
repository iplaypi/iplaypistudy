#!/bin/bash
# 日志路径，如果安装失败需要查看日志是否有异常报错信息
export log_path=/etc/auto_deploy_shadowsocks.log
# 设置端口号,从键盘接收参数输入,默认为2018,-e参数转义开启高亮显示
echo -n -e '\033[36mPlease enter PORT[2018 default]:\033[0m'
read port
if [ ! -n "$port" ];then
    echo "port will be set to 2018"
    port=2018
else
    echo "port will be set to $port"
fi
# 设置密码,从键盘接收参数输入,默认为pengfeivpn,-e参数转义开启高亮显示
echo -n -e '\033[36mPlease enter PASSWORD[pengfeivpn default]:\033[0m'
read pwd
if [ ! -n "$pwd" ];then
    echo "password will be set to 123456"
    pwd=pengfeivpn
else
    echo "password will be set to $pwd"
fi
# 创建shadowsocks.json配置文件,只开一个端口,server可以是0.0.0.0
cat>/etc/shadowsocks.json<<EOF
{
    "server":"0.0.0.0",
    "server_port":$port,
    "local_address": "127.0.0.1",
    "local_port":1080,
    "password":"$pwd",
    "timeout":300,
    "method":"aes-256-cfb",
    "fast_open": false
}
EOF
# 安装shadowsocks/防火墙,携带-y参数表示自动同意安装,无需交互
# 日志全部输出到上面指定的日志文件中
echo "" >> ${log_path}
echo "********************************" >> ${log_path}
echo "start deploy shadowsocks,date is:"$(date +%Y-%m-%d %X) >> ${log_path}
echo "********************************" >> ${log_path}
echo "" >> ${log_path}
echo "******************start install m2crypto" >> ${log_path}
ret=`yum install -y m2crypto python-setuptools >> ${log_path} 2>&1`
echo "" >> ${log_path}
echo "******************start install pip" >> ${log_path}
ret=`easy_install pip >> ${log_path} 2>&1`
echo "" >> ${log_path}
echo "******************start install shadowsocks" >> ${log_path}
ret=`pip install shadowsocks >> ${log_path} 2>&1`
echo "" >> ${log_path}
echo "******************start install firewalld" >> ${log_path}
ret=`yum install -y firewalld >> ${log_path} 2>&1`
echo "" >> ${log_path}
echo "******************start start firewalld" >> ${log_path}
ret=`systemctl start firewalld >> ${log_path} 2>&1`
echo "" >> ${log_path}
echo "******************start reload firewall" >> ${log_path}
# 开启端口
ret=`firewall-cmd --permanent --zone=public --add-port=22/tcp >> ${log_path} 2>&1`
ret=`firewall-cmd --permanent --zone=public --add-port=$port/tcp >> ${log_path} 2>&1`
ret=`firewall-cmd --reload >> ${log_path} 2>&1`
# 如果有相同功能的进程则先杀死,$?表示上个命令的退出状态,或者函数的返回值
ps -ef | grep ssserver | grep shadowsocks | grep -v grep
if [ $? -eq 0 ];then
    ps -ef | grep ssserver | grep shadowsocks | awk '{ print $2 }' | xargs kill -9
fi
# 后台运行
/usr/bin/ssserver -c /etc/shadowsocks.json -d start
# 成功
if [ $? -eq 0 ];then
clear
cat<<EOF
***************Congratulation!*****************
shadowsocks deployed successfully!

PORT:$port
PASSWORD:$pwd
METHOD: aes-256-cfb

*****************JUST ENJOY IT!****************
EOF
# 失败
else
clear
cat<<EOF
**************Failed,retry please!*************

cat /etc/ss.log to get something you need.

**************Failed,retry please!*************
EOF
fi
