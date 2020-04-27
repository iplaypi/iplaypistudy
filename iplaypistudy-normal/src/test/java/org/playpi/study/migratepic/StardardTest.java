package org.playpi.study.migratepic;

import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 临时测试
 */
public class StardardTest {

    @Test
    public void xxTest() {
        try {
            List<String> lines = FileUtils.readLines(new File("d:\\wb.txt"));
            List<String> out = Lists.newArrayList();
            out.add("mid,uid,发表时间,采集时间,第一次采集时间,入库时间,第一次入库时间");
            for (String line : lines) {
                List<String> lineList = Lists.newArrayList(line.split(","));
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    String a = sdf2.format(sdf1.parse(lineList.get(2)));
                    lineList.set(2, a);
                    a = sdf2.format(new Date(Long.valueOf(lineList.get(3))));
                    lineList.set(3, a);
                    a = sdf2.format(sdf1.parse(lineList.get(4)));
                    lineList.set(4, a);
                    a = sdf2.format(sdf1.parse(lineList.get(5)));
                    lineList.set(5, a);
                    a = sdf2.format(sdf1.parse(lineList.get(6)));
                    lineList.set(6, a);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                out.add(StringUtils.join(lineList, ","));
            }
            FileUtils.writeLines(new File("d:\\wb_out.csv"), out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
