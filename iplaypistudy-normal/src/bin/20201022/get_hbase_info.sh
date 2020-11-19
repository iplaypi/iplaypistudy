#!bash/bin
path='hdfs://host:8020/apps/hbase/data/data/default/'
out=./hbase_cluster_table_info.csv
echo "table_name,table_size GB,table_region_size,平均单个Region大小 GB" > $out
#原始分隔符
ORIGIN_IFS="$IFS"
#换行分隔符
IFS=$'\n'
for table_path_info in `hdfs dfs -ls $path`
do
  #echo $table_path_info
  OLD_IFS="$IFS"
  IFS="$ORIGIN_IFS"
  arr=($table_path_info)
  IFS="$OLD_IFS"
  table_path=${arr[7]}
  table=${table_path##*/}
  if [[ $table == XX_YY_* ]];
  then
    echo ----$table----
    table_info=`hdfs dfs -du -s -h $path$table`
    #echo $table_info
    table_size=${table_info%hdfs://*}
    table_size_unit=${table_size#* }
    table_size_unit=`echo $table_size_unit |sed 's/ //g'`
    table_size=${table_size%% *}
    # 统一转为GB单位
    if [[ "M" == $table_size_unit ]];
    then
      table_size=`awk 'BEGIN{printf "%.2f\n",'$table_size' / '1024'}'`
    elif [[ "T" == $table_size_unit ]];
    then
      table_size=`awk 'BEGIN{printf "%.2f\n",'$table_size' * '1024'}'`
    fi
    table_region_size=`hdfs dfs -du -s -h $path$table/* |wc -l`
    table_region_size=`expr $table_region_size - 2`
    # 计算平均单个Region大小
    table_region_avg_size=`awk 'BEGIN{printf "%.2f\n",'$table_size' / '$table_region_size'}'`
    echo ----$table,$table_size,$table_region_size,$table_region_avg_size----
    echo "$table,$table_size,$table_region_size,$table_region_avg_size" >> $out
  fi
done
IFS="$ORIGIN_IFS"
