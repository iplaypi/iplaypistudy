package org.playpi.study.test;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.playpi.study.base.IplaypiStudyConfig;

import java.io.IOException;
import java.util.List;

/**
 * HBase 测试类
 */
@Slf4j
public class TestHBase {

    public static void main(String[] args) {
        TestHBase testHBase = new TestHBase();
        testHBase.testGet();
    }

    /**
     * HBase Java API Get测试
     */
    public void testGet() {
        String hTableName = "TB_HBASE_STUDENT";
        IplaypiStudyConfig configuration = IplaypiStudyConfig.getInstance();
        byte[] cfByte = "cf".getBytes();
        byte[] col01Byte = "name".getBytes();
        byte[] col02Byte = "age".getBytes();
        try {
            // 构造查询请求,2条数据,多个版本
            List<Get> getList = Lists.newArrayList();
            Get get = new Get(Bytes.toBytes("row01"));
            get.addColumn(cfByte, col01Byte);
            // 设置最大版本数,默认为1
            get.setMaxVersions(3);
            getList.add(get);
            Get get2 = new Get(Bytes.toBytes("row02"));
            get2.addColumn(cfByte, col01Byte);
            get2.addColumn(cfByte, col02Byte);
            getList.add(get2);
            // 发送请求,获取结果
            HTable hTable = new HTable(configuration, hTableName);
            Result[] resultArr = hTable.get(getList);
            /**
             * 以下有两种解析结果的方法
             * 1-通过Result类的getRow()和getValue()两个方法,只能获取最新版本
             * 2-通过Result类的rawCells()方法返回一个Cell数组,可以获取多个版本
             * 注意,高版本不再建议使用KeyValue的方式,注释中有说明
             */
            // 1-
            log.info("====get result by first method");
            for (Result result : resultArr) {
                log.info("");
                log.info("--------");
                String rowStr = Bytes.toString(result.getRow());
                log.info("====row:[{}]", rowStr);
                // 如果包含name列,则获取输出
                if (result.containsColumn(cfByte, col01Byte)) {
                    String valStr = Bytes.toString(result.getValue(cfByte, col01Byte));
                    log.info("====name:[{}],getValue", valStr);
                    // 以下方式不建议使用,但是可以获取多版本
                    List<KeyValue> keyValueList = result.getColumn(cfByte, col01Byte);
                    for (KeyValue keyValue : keyValueList) {
                        log.info("====name:[{}],getColumn -> getValue", Bytes.toString(keyValue.getValue()));
                    }
                }
                // 如果包含age列,则获取输出
                if (result.containsColumn(cfByte, col02Byte)) {
                    String valStr = Bytes.toString(result.getValue(cfByte, col02Byte));
                    log.info("====age:[{}],getValue", valStr);
                    // 以下方式不建议使用,但是可以获取多版本
                    List<KeyValue> keyValueList = result.getColumn(cfByte, col02Byte);
                    for (KeyValue keyValue : keyValueList) {
                        log.info("====age:[{}],getColumn -> getValue", Bytes.toString(keyValue.getValue()));
                    }
                }
            }
            // 2-
            log.info("");
            log.info("====get result by second method");
            for (Result result : resultArr) {
                log.info("");
                log.info("--------");
                String rowStr = Bytes.toString(result.getRow());
                log.info("====row:[{}]", rowStr);
                // name列
                List<Cell> cellList = result.getColumnCells(cfByte, col01Byte);
                // 1个cell就是1个版本
                for (Cell cell : cellList) {
                    // 高版本不建议使用
                    log.info("====name:[{}],getValue", Bytes.toString(cell.getValue()));
                    // getValueArray:数据的byte数组
                    // getValueOffset:rowkey在数组中的索引下标
                    // getValueLength:rowkey的长度
                    String valStr = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                    log.info("====name:[{}],[getValueArray,getValueOffset,getValueLength]", valStr);
                    log.info("====timestamp:[{}],cell", cell.getTimestamp());
                }
                // age列不演示了,省略...
            }
        } catch (IOException e) {
            log.error("!!!!error: " + e.getMessage(), e);
        }
    }
}
