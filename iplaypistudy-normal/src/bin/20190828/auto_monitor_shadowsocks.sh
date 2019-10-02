#!/bin/bash
# 注意本脚本中的换行符号,一律使用\n的形式,否则会引起错误
# 日志路径,如果安装失败需要查看日志,是否有异常/报错信息
export log_path=/etc/auto_monitor_shadowsocks.log
# 设置执行周期,10分钟
# export circle_time=10m
export circle_time=5s
# 设置ip,从键盘接收参数输入,必须输入,-e参数转义开启高亮显示
echo -n -e '\033[36mPlease enter IP[must enter]:\033[0m'
read ip
if [ ! -n "$ip" ];then
    echo "Please enter IP"
    exit 1
else
    echo "ip will be set to $ip"
fi
# 设置端口号,从键盘接收参数输入,必须输入,-e参数转义开启高亮显示
echo -n -e '\033[36mPlease enter PORT[must enter]:\033[0m'
read port
if [ ! -n "$port" ];then
    echo "Please enter PORT"
    exit 1
else
    echo "port will be set to $port"
fi
# 变量,标记是否通知以及通知内容
notice=0
notice_msg=""
# 变量,标记ip/端口的失败次数
ip_fail_num=0
port_fail_num=0
# while循环,每10分钟执行一次
while :
do
# 查看ip是否正常
ping=`ping -c 1 $ip |grep loss |awk '{print $6}' |awk -F "%" '{print $1}'`
if [ $ping -eq 100 ];then
ip_fail_num=`expr $ip_fail_num + 1`
echo ping [$ip] at $(date +%Y-%m-%d%t%X) fail >> $log_path
notice=1
notice_msg=`echo ping [$ip] at $(date +%Y-%m-%d%t%X) 失败，累计次数：[$ip_fail_num]，请更换主机`
else
echo ping [$ip] at $(date +%Y-%m-%d%t%X) ok >> $log_path
ip_fail_num=0
# 接着判断端口是否可用,使用nc工具,超时时间为20秒
`nc -v -z -w 20 $ip $port`
if [ 0 -ne $? ];then
port_fail_num=`expr $port_fail_num + 1`
echo nc [$ip:$port] at $(date +%Y-%m-%d%t%X) fail >> $log_path
if [ $port_fail_num -gt 3 ];then
notice=1
notice_msg=`echo nc [$ip:$port] at $(date +%Y-%m-%d%t%X) 失败，累计次数：[$port_fail_num]，请更换端口`
fi
else
port_fail_num=0
fi
fi
# 建议开启server酱自动通知,推送到微信
# 不开启请把以下内容注释掉,注释内容持续到'server酱通知完成'
# 关于server酱的使用请参考:https://sc.ftqq.com
# 注意server_key不要泄露,泄漏后可以去官网重置
if [ 1 -eq $notice ];then
echo "**************开始处理server酱通知"
server_key=SCU60861T303e1c479df6cea9e95fc54d210232565d7dbbf075750
# 传输2个参数:text/desp,desp使用markdown语法(注意换行符要使用2个换行)
cat>./shadowsocks_msg.txt<<EOF
text=shadowsocks定时监控服务消息
&desp=
$notice_msg
EOF
curl -X POST --data-binary @./shadowsocks_msg.txt https://sc.ftqq.com/$server_key.send >> $log_path
echo ""
echo "**************server酱通知处理完成"
notice=0
notice_msg=""
fi
sleep $circle_time
done
