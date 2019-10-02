#!/bin/bash
# 注意本脚本中的换行符号,一律使用\n的形式,否则会引起错误
# 日志路径,如果安装失败需要查看日志,是否有异常/报错信息
export log_path=/etc/auto_restart_shadowsocks.log
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
echo "****************start generate /etc/shadowsocks.json"
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
echo "****************start open port"
# 开启端口
ret=`firewall-cmd --permanent --zone=public --add-port=$port/tcp >> ${log_path} 2>&1`
ret=`firewall-cmd --reload >> ${log_path} 2>&1`
# 正常停掉 shadowsocks 服务
echo "****************start stop shadowsocks"
/usr/bin/ssserver -c /etc/shadowsocks.json -d stop
echo "****************start check shadowsocks"
# 如果有相同功能的进程则先杀死,$?表示上个命令的退出状态,或者函数的返回值
ps -ef | grep ssserver | grep shadowsocks | grep -v grep
if [ $? -eq 0 ];then
    ps -ef | grep ssserver | grep shadowsocks | awk '{ print $2 }' | xargs kill -9
fi
# 后台启动,-d表示守护进程
/usr/bin/ssserver -c /etc/shadowsocks.json -d start
# 启动成功
if [ $? -eq 0 ];then
# 获取本机ip地址
ip=`ip addr | grep 'state UP' -A2 | tail -n1 | awk '{print $2}' | cut -f1 -d '/'`
clear
cat<<EOF
***************Congratulation!*****************
shadowsocks deployed successfully!

IP:$ip
PORT:$port
PASSWORD:$pwd
METHOD:aes-256-cfb

*****************JUST ENJOY IT!****************
EOF
# 建议开启server酱自动通知,推送到微信,就可以直接复制信息转发给别人了
# 不开启请把以下内容注释掉,注释内容持续到'server酱通知完成'
# 关于server酱的使用请参考:https://sc.ftqq.com
# 注意server_key不要泄露,泄漏后可以去官网重置
echo "**************开始处理server酱通知"
server_key=SCU60861T303e1c479df6cea9e95fc54d210232565d7dbbf075750
# 传输2个参数:text/desp,desp使用markdown语法(注意换行符要使用2个换行)
cat>./shadowsocks_msg.txt<<EOF
text=shadowsocks服务部署启动完成
&desp=
- IP地址：$ip

- 端口号：$port

- 密码：$pwd

- 加密方式：aes-256-cfb
EOF
curl -X POST --data-binary @./shadowsocks_msg.txt  https://sc.ftqq.com/$server_key.send
echo ""
echo "**************server酱通知处理完成"
# 失败
else
clear
cat<<EOF
**************Failed,retry please!*************

cat /etc/ss.log to get something you need.

**************Failed,retry please!*************
EOF
fi
