package org.playpi.study.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * 日期工具类
 */
@Slf4j
public class DateUtil {

    /**
     * 由格式化日期转为时间戳
     *
     * @param formatDate
     * @param format
     * @return
     */
    public static Long parseTimestamp(String formatDate, String format) {
        Long result = null;
        if (StringUtils.isEmpty(formatDate) || StringUtils.isEmpty(format)) {
            return result;
        }
        try {
            result = new SimpleDateFormat(format).parse(formatDate).getTime();
        } catch (ParseException e) {
            log.error("!!!!日期转换错误,formatDate:[{}],format:[{}]", formatDate, format);
            log.error("!!!!日期转换错误: " + e.getMessage(), e);
        }
        return result;
    }

    /**
     * 由时间戳转为格式化日期
     *
     * @param timestamp
     * @param format
     * @return
     */
    public static String formatDate(Long timestamp, String format) {
        String result = null;
        if (Objects.isNull(timestamp) || StringUtils.isEmpty(format)) {
            return result;
        }
        try {
            result = new SimpleDateFormat(format).format(timestamp);
        } catch (Exception e) {
            log.error("!!!!日期转换错误,timestamp:[{}],format:[{}]", timestamp, format);
            log.error("!!!!日期转换错误: " + e.getMessage(), e);
        }
        return result;
    }

    @Test
    public void dateTest() {
        String format = "yyyyMMdd";
        String dateStr = "2017-07-01";
        Long time = parseTimestamp(dateStr, format);
        log.info("====dateStr:[{}] -> time:[{}]", dateStr, time);
        String date = formatDate(time, format);
        log.info("====time:[{}],dateStr:[{}]", time, date);
    }

}
