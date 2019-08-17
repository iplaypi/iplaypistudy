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
        String hTableName = "TB_HBASE_TEST";
        IplaypiStudyConfig configuration = IplaypiStudyConfig.getInstance();
        try {
            HTable hTable = new HTable(configuration, hTableName);
            // 构造查询请求,2条数据,多个版本
            List<Get> getList = Lists.newArrayList();
            Get get = new Get(Bytes.toBytes("row01"));
            get.addColumn("cf01".getBytes(), "col01".getBytes());
            // 设置最大版本数
            get.setMaxVersions(3);
            getList.add(get);
            Get get2 = new Get(Bytes.toBytes("row02"));
            get2.addColumn("cf01".getBytes(), "col01".getBytes());
            get2.addColumn("cf01".getBytes(), "col02".getBytes());
            getList.add(get2);
            // 发送请求,获取结果
            Result[] resultArr = hTable.get(getList);
            /**
             * 以下有两种解析结果的方法
             * 1-通过Result类的getRow()和getValue()两个方法,只能获取最新版本
             * 2-通过Result类的rawCells()方法返回一个Cell数组,可以获取多个版本
             * 注意,高版本不再建议使用KeyValue的方式,注释掉的代码
             */
            // 1-
            log.info("====get result by first method");
            for (Result result : resultArr) {
                log.info("");
                log.info("--------");
                String rowStr = Bytes.toString(result.getRow());
                log.info("====row:[{}]", rowStr);
                // 如果包含col01列,则获取输出
                if (result.containsColumn(Bytes.toBytes("cf01"), Bytes.toBytes("col01"))) {
                    String valStr = Bytes.toString(result.getValue(Bytes.toBytes("cf01"), Bytes.toBytes("col01")));
                    log.info("====getValue -> valStr:[{}]", valStr);
                    // 以下方式不建议使用,但是可以获取多版本
                    List<KeyValue> keyValueList = result.getColumn(Bytes.toBytes("cf01"), Bytes.toBytes("col01"));
                    for (KeyValue keyValue : keyValueList) {
                        log.info("====getColumn -> getValue -> valStr:[{}]", Bytes.toString(keyValue.getValue()));
                    }
                }
                // 如果包含col02列,则获取输出
                if (result.containsColumn(Bytes.toBytes("cf01"), Bytes.toBytes("col02"))) {
                    String valStr = Bytes.toString(result.getValue(Bytes.toBytes("cf01"), Bytes.toBytes("col02")));
                    log.info("====getColumn -> valStr:[{}]", valStr);
                    // 以下方式不建议使用,但是可以获取多版本
                    List<KeyValue> keyValueList = result.getColumn(Bytes.toBytes("cf01"), Bytes.toBytes("col02"));
                    for (KeyValue keyValue : keyValueList) {
                        log.info("====getColumn -> getValue -> valStr:[{}]", Bytes.toString(keyValue.getValue()));
                    }
                }
            }
            // 2-
            log.info("====get result by second method");
            for (Result result : resultArr) {
                log.info("");
                log.info("--------");
                List<Cell> cellList = result.getColumnCells("cf01".getBytes(), "col01".getBytes());
                // 1个cell就是1个版本
                for (Cell cell : cellList) {
                    // getValueArray:数据的byte数组
                    // getValueOffset:rowkey在数组中的索引下标
                    // getValueLength:rowkey的长度
                    String valStr = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                    log.info("====getColumnCells -> [getValueArray,getValueOffset,getValueLength] -> valStr:[{}]", valStr);
                    log.info("====cell timestamp:[{}]", cell.getTimestamp());
                }
                // col02列不演示了,省略
            }
        } catch (IOException e) {
            log.error("!!!!error: " + e.getMessage(), e);
        }
    }
}
