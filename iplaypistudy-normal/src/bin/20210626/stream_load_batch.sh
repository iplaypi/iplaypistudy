#!/bin/sh
#拆分文件，导入数据库
# 判断部署脚本传入的参数个数
if [ $# -ne 2 ]; then
  echo "Usage: sh stream_load_batch.sh <data path> <table name>\n"
  exit
fi
data_path=$1
ip_port=fe-ui-host:8030
db_name=db_xx
table_name=$2
echo "data_path: $data_path, ip_port: $ip_port, db_name: $db_name, table_name: $table_name"
#拆分文件
split -l 200000 $data_path -d -a 3 temp_data_
for file in `ls .`
do
  if [ -f "$file" ] && [[ $file == temp_data_* ]];then
    #处理文件格式
    sed -i 's/}$/},/g' "$file"
    sed -i '1s/^/[/' "$file"
    sed -i '$s/,$/]/' "$file"
    #导入doris
    label=doris_data_`date '+%Y%m%d%H%M%S'`
    echo "stream_load label: $label"
    curl --location-trusted -u test:test -T "$file" -H "format: json" -H "label:$label" -H "strip_outer_array:true" http://"$ip_port"/api/"$db_name"/"$table_name"/_stream_load
    rm "$file"
    sleep 1s
  else
    echo "skip $file"
  fi
done
