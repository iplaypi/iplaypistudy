package org.playpi.study.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 微博工具类
 * 微博url示例:http://weibo.com/2434411070/E9se2kp4e
 * <p>
 * 处理微博url/mid/uid/url相关
 * 微博url是指http开头的微博连接,例如:http://weibo.com/2434411070/E9se2kp4e
 * mid是指微博唯一标识,例如:4022534544862922
 * uid是指微博用户唯一标识,例如:2434411070
 * url是指微博url中结尾的一串字符,例如:E9se2kp4e
 */
@Slf4j
public class WeiboUtil {

    /**
     * 所有的数字和字母
     */
    static String[] str62key = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n",
            "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    /**
     * 合法微博url正则
     */
    private static final String REGEX_WEIBO_URL = "https?://(www\\.)?weibo\\.com/[0-9]+/[0-9a-zA-Z]+";
    private static final Pattern PATTERN_WEIBO_URL = Pattern.compile(REGEX_WEIBO_URL);
    private static final Pattern PATTERN_WEIBO_DIGITAL = Pattern.compile("/([0-9]+)/");
    ;

    /**
     * 根据uid/mid拼接微博链接
     *
     * @param uid
     * @param mid
     * @return
     */
    public static String getWeiboUrlByUidMid(String uid, String mid) {
        return String.format("http://weibo.com/%s/%s", uid, mid2url(mid));
    }

    /**
     * 从微博url中抽取uid
     *
     * @param weiboUrl
     * @return
     */
    public static String getUidByWeiboUrl(String weiboUrl) {
        String uid = "";
        if (!StringUtils.isEmpty(weiboUrl)) {
            Matcher matcher = PATTERN_WEIBO_URL.matcher(weiboUrl);
            if (matcher.find()) {
                weiboUrl = weiboUrl.substring(matcher.start(), matcher.end());
                // 抽取其中的数字串
                Matcher digitalMatcher = PATTERN_WEIBO_DIGITAL.matcher(weiboUrl);
                if (digitalMatcher.find()) {
//                    uid = digitalMatcher.group();
                    uid = weiboUrl.substring(digitalMatcher.start() + 1, digitalMatcher.end() - 1);
                }
            }
        }
        return uid;
    }

    /**
     * 从微博url中抽取mid
     *
     * @param weiboUrl
     * @return
     */
    public static String getMidByWeiboUrl(String weiboUrl) {
        String mid = "";
        if (!StringUtils.isEmpty(weiboUrl)) {
            Matcher matcher = PATTERN_WEIBO_URL.matcher(weiboUrl);
            if (matcher.find()) {
                // 先抽取微博url,再转换mid
                weiboUrl = weiboUrl.substring(matcher.start(), matcher.end());
                int index = weiboUrl.lastIndexOf("/");
                String url = weiboUrl.substring(index + 1);
                mid = url2mid(url);
            }
        }
        return mid;
    }

    public static String url2midNew(String url) {
        String mid = "";
        int index = url.length();
        while (index > 0) {
            String substr = "";
            if (index - 4 < 0) {
                substr = url.substring(0, index);
                mid = int62to10New(substr, false) + mid;
            } else {
                substr = url.substring(index - 4, index);
                mid = int62to10New(substr, true) + mid;
            }
            index -= 4;
        }
        return mid;
    }

    /**
     * 62进制值转换为10进制
     *
     * @param {String} int62 62进制值
     * @return {int} 10进制值
     */
    public static String int62to10New(String int62, boolean needPending) {
        int res = 0;
        int base = 62;
        for (int i = 0; i < int62.length(); ++i) {
            res *= base;
            char charofint62 = int62.charAt(i);
            if (charofint62 >= '0' && charofint62 <= '9') {
                int num = charofint62 - '0';
                res += num;
            } else if (charofint62 >= 'a' && charofint62 <= 'z') {
                int num = charofint62 - 'a' + 10;
                res += num;
            } else if (charofint62 >= 'A' && charofint62 <= 'Z') {
                int num = charofint62 - 'A' + 36;
                res += num;
            } else {
                System.err.println("this is not a 62base number");
                return null;
            }
        }
        String resstr = String.valueOf(res);
        if (needPending) {
            while (resstr.length() < 7) {
                resstr = "0" + resstr;
            }
        }
        return resstr;
    }

    public static String genHashId(String mid) {
        String url = "";
        // if the high position need to be pended by zero.
        boolean needPending = true;
        for (int i = mid.length() - 7; i > -7; i = i - 7) {//从最后往前以7字节为一组读取mid
            int offset1 = i < 0 ? 0 : i;
            int offset2 = i + 7;
            String num = mid.substring(offset1, offset2);
            if (i < 0)
                needPending = false;

            num = int10to62New(Integer.parseInt(num), needPending);
            url = num + url;
        }

        return url;
    }

    /**
     * 10进制值转换为62进制，需要对得出的62base number补齐4位
     *
     * @param {String} int10 10进制值
     * @return {String} 62进制值
     */
    public static String int10to62New(int int10, boolean needPending) {
        String s62 = "";
        int r = 0;
        while (int10 != 0 && s62.length() < 100) {
            r = int10 % 62;
            s62 = str62key[r] + s62;
            int10 = (int) Math.floor(int10 / 62);
        }
        if (needPending)
            while (s62.length() < 4) {
                s62 = "0" + s62;
            }
        return s62;
    }

    /**
     * url转化成mid的值
     * 注意这里的url不是指完整的url,而是唯一标识
     * 例如微博链接:https://weibo.com/6004173277/GEFG5nZX0,
     * url指的是:GEFG5nZX0,
     * 转为mid是:4296933335719686
     *
     * @param url
     * @return
     */
    public static String url2mid(String url) {
        String mid = "";
        String k = url.toString().substring(3, 4);//用于第四位为0时的转换
        if (!k.equals("0")) {
            for (int i = url.length() - 4; i > -4; i = i - 4) {//分别以四个为一组
                int offset1 = i < 0 ? 0 : i;
                int offset2 = i + 4;
                String str = url.toString().substring(offset1, offset2);
                str = str62to10(str);//String类型的转化成十进制的数
                // 若不是第一组，则不足7位补0
                if (offset1 > 0) {
                    while (str.length() < 7) {
                        str = '0' + str;
                    }
                }
                mid = str + mid;
            }
        } else {
            for (int i = url.length() - 4; i > -4; i = i - 4) {
                int offset1 = i < 0 ? 0 : i;
                int offset2 = i + 4;
                if (offset1 > -1 && offset1 < 1 || offset1 > 4) {
                    String str = url.toString().substring(offset1, offset2);
                    str = str62to10(str);
                    // 若不是第一组，则不足7位补0
                    if (offset1 > 0) {
                        while (str.length() < 7) {
                            str = '0' + str;
                        }
                    }
                    mid = str + mid;
                } else {
                    String str = url.toString().substring(offset1 + 1, offset2);
                    str = str62to10(str);
                    // 若不是第一组，则不足7位补0
                    if (offset1 > 0) {
                        while (str.length() < 7) {
                            str = '0' + str;
                        }
                    }
                    mid = str + mid;
                }
            }
        }
        return mid;
    }

    /**
     * mid转换成url编码以后的值
     *
     * @param mid
     * @return
     */
    public static String mid2url(String mid) {
        String url = "";
        for (int j = mid.length() - 7; j > -7; j = j - 7) {//以7个数字为一个单位进行转换
            int offset3 = j < 0 ? 0 : j;
            int offset4 = j + 7;
            // String l = mid.substring(mid.length() - 14, mid.length() - 13);
            if ((j > 0 && j < 6) && (mid.substring(mid.length() - 14, mid.length() - 13).equals("0") && mid.length() == 19)) {
                String num = mid.toString().substring(offset3 + 1, offset4);
                num = int10to62(Integer.valueOf(num));//十进制转换成62进制
                url = 0 + num + url;
                if (url.length() == 9) {
                    url = url.substring(1, url.length());
                }
            } else {
                String num = mid.toString().substring(offset3, offset4);
                num = int10to62(Integer.valueOf(num));
                url = num + url;
            }
        }

        return url;
    }

    /**
     * 62进制转换成10进制
     *
     * @param str
     * @return
     */
    public static String str62to10(String str) {
        String i10 = "0";
        int c = 0;
        for (int i = 0; i < str.length(); i++) {
            int n = str.length() - i - 1;
            String s = str.substring(i, i + 1);
            for (int k = 0; k < str62key.length; k++) {
                if (s.equals(str62key[k])) {
                    int h = k;
                    c += (int) (h * Math.pow(62, n));
                    break;
                }
            }
            i10 = String.valueOf(c);
        }
        return i10;
    }

    /**
     * 10进制转换成62进制
     *
     * @param int10
     * @return
     */
    public static String int10to62(double int10) {
        String s62 = "";
        int w = (int) int10;
        int r = 0;
        int a = 0;
        while (w != 0) {
            r = (int) (w % 62);
            s62 = str62key[r] + s62;
            a = (int) (w / 62);
            w = (int) Math.floor(a);
        }
        return s62;
    }

    /**
     * 测试用例
     */
    @Test
    public void test() {
        log.info("====getWeiboUrl:[{}]", getWeiboUrlByUidMid("2434411070", "4022534544862922"));
        log.info("====getUidByWeiboUrl:[{}]", getUidByWeiboUrl("http://weibo.com/2434411070/E9se2kp4e"));
        log.info("====getMidByWeiboUrl:[{}]", getMidByWeiboUrl("http://weibo.com/2434411070/E9se2kp4e"));

        log.info("====url2mid:[{}]", url2mid("E9se2kp4e"));
        log.info("====mid2url:[{}]", mid2url("2011101011002749014"));
    }
}
