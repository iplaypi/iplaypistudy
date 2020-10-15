#!/bin/bash
#计算kafka消费偏移量,并判断数据是否积压,发送通知
#存放消费组描述,group,topic,limit的文件
file="./kafka_monitor_group_topic.txt"
#zk地址,测试环境dev1:2181
zkconnect="dev1:2181"
#原始分隔符
ORIGIN_IFS="$IFS"
#换行分隔符
IFS=$'\n'
#无限循环
while :
do
    #读取文件每一行
    for line in `cat "$file"`
    do
    if [[ "$line" =~ "#" ]];then
        #跳过注释
        #echo "skip comment:$line"
        :
    else
        #分割字符串获取4个参数:消费组描述,消费组名,topic名称,数据积压上限
        OLD_IFS="$IFS"
        IFS="$ORIGIN_IFS"
        array=($line)
        IFS="$OLD_IFS"
        description="${array[0]}"
        group="${array[1]}"
        topic="${array[2]}"
        limit="${array[3]}"
        total=0
        #查询积压信息,累加求和
        for result in `kafka-run-class.sh kafka.tools.ConsumerOffsetChecker --group "$group"  --topic "$topic" --zkconnect "$zkconnect"`;
        do
        if [[ "$result" =~ "consumer_" ]];then
            OLD_IFS="$IFS"
            IFS="$ORIGIN_IFS"
            array=($result)
            IFS="$OLD_IFS"
            factor="${array[5]}"
            #是数字才累加
            expr $1 "+" 10 &> /dev/null
            if [ $? -eq 0 ];then
                total=`expr $total + $factor`
            fi
        fi
        done
        #比较大小,大于积压上限发送通知
        if [ "$total" -gt "$limit" ];then
            echo "!!!![$description,$group,$topic],配置积压数据量上限->[$limit],当前积压数据量->[$total]"
            cat>./kafka_monitor_msg.txt<<EOF
text=Kakfa消费积压啦$total
&desp=
- 消费组描述：$description

- 消费组名：$group

- topic：$topic

- 配置积压数据量上限：$limit

- 当前积压数据量：$total
EOF
            server_key=SCU60861T303e1c479df6cea9e95fc54d210232565d7dbbfxxyyzz
            curl -X POST --data-binary @./kafka_monitor_msg.txt  https://sc.ftqq.com/"$server_key".send
            echo ""
        #else
            # echo "====[$group,$topic] is normal"
        fi
    fi
    done
    #休息10分钟
    sleep 10m
done
#还原分隔符
IFS="$ORIGIN_IFS"