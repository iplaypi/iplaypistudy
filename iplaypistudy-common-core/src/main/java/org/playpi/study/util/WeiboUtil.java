package org.playpi.study.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 微博工具类
 * url示例:https://weibo.com/3086148515/I1IGF4Ud1
 * <p>
 * 处理url/mid/uid/id相关
 * <p>
 * url是指以http/https开头的微博链接,例如:https://weibo.com/3086148515/I1IGF4Ud1
 * mid是指微博唯一标识,可以和id转换,例如:4404101091169383(也可以理解为mobile id,可以和murl转换)
 * uid是指微博用户唯一标识,例如:3086148515
 * id是指微博url中结尾的一串字符,可以和mid转换,例如:I1IGF4Ud1
 * <p>
 * murl,即mobile url,移动端url,格式:https://m.weibo.cn/status/idhuo或者https://m.weibo.cn/status/mid
 * 专为客户端设计,适合使用手机/平板的浏览器打开,排版显示友好,如果使用电脑的浏览器打开,排版显示不友好
 * 例如:https://m.weibo.cn/status/I1IGF4Ud1或者https://m.weibo.cn/status/4404101091169383
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
     * 合法url正则
     */
    private static final String REGEX_WEIBO_URL = "https?://(www\\.)?weibo\\.com/[0-9]+/[0-9a-zA-Z]+";
    private static final Pattern PATTERN_WEIBO_URL = Pattern.compile(REGEX_WEIBO_URL);
    /**
     * 数字正则
     */
    private static final Pattern PATTERN_WEIBO_DIGITAL = Pattern.compile("/([0-9]+)/");

    /**
     * 根据uid/mid拼接url
     *
     * @param uid
     * @param mid
     * @return
     */
    public static String getUrlByUidMid(String uid, String mid) {
        return String.format("https://weibo.com/%s/%s", uid, mid2id(mid));
    }

    /**
     * 从url中抽取uid
     *
     * @param url
     * @return
     */
    public static String getUidByUrl(String url) {
        String uid = "";
        if (!StringUtils.isEmpty(url)) {
            Matcher matcher = PATTERN_WEIBO_URL.matcher(url);
            if (matcher.find()) {
                url = url.substring(matcher.start(), matcher.end());
                // 抽取其中的数字串
                Matcher digitalMatcher = PATTERN_WEIBO_DIGITAL.matcher(url);
                if (digitalMatcher.find()) {
//                    uid = digitalMatcher.group();
                    uid = url.substring(digitalMatcher.start() + 1, digitalMatcher.end() - 1);
                }
            }
        }
        return uid;
    }

    /**
     * 从url中抽取mid
     * 先抽取id再转换
     *
     * @param url
     * @return
     */
    public static String getMidByUrl(String url) {
        String mid = "";
        if (!StringUtils.isEmpty(url)) {
            Matcher matcher = PATTERN_WEIBO_URL.matcher(url);
            if (matcher.find()) {
                // 先抽取url规范部分(例如去除?后面的参数),再抽取id,再转换为mid
                url = url.substring(matcher.start(), matcher.end());
                int index = url.lastIndexOf("/");
                String id = url.substring(index + 1);
                mid = id2mid(id);
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
     * id转化成mid的值
     *
     * @param id
     * @return
     */
    public static String id2mid(String id) {
        String mid = "";
        String k = id.toString().substring(3, 4);//用于第四位为0时的转换
        if (!k.equals("0")) {
            for (int i = id.length() - 4; i > -4; i = i - 4) {//分别以四个为一组
                int offset1 = i < 0 ? 0 : i;
                int offset2 = i + 4;
                String str = id.toString().substring(offset1, offset2);
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
            for (int i = id.length() - 4; i > -4; i = i - 4) {
                int offset1 = i < 0 ? 0 : i;
                int offset2 = i + 4;
                if (offset1 > -1 && offset1 < 1 || offset1 > 4) {
                    String str = id.toString().substring(offset1, offset2);
                    str = str62to10(str);
                    // 若不是第一组，则不足7位补0
                    if (offset1 > 0) {
                        while (str.length() < 7) {
                            str = '0' + str;
                        }
                    }
                    mid = str + mid;
                } else {
                    String str = id.toString().substring(offset1 + 1, offset2);
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
     * mid转换成id
     *
     * @param mid
     * @return
     */
    public static String mid2id(String mid) {
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
        String urlStr = "https://weibo.com/3086148515/I1IGF4Ud1";
        String idStr = "I1IGF4Ud1";
        String midStr = "4404101091169383";
        String uidStr = "3086148515";
        String murlStr1 = "https://m.weibo.cn/status/I1IGF4Ud1";
        String murlStr2 = "https://m.weibo.cn/status/4404101091169383";
        log.info("====getUrlByUidMid,uid:[{}],mid:[{}],url:[{}]", uidStr, midStr, getUrlByUidMid(uidStr, midStr));
        log.info("====getUidByUrl,url:[{}],uid:[{}]", urlStr, getUidByUrl(urlStr));
        log.info("====getMidByUrl,url:[{}], mid:[{}]", urlStr, getMidByUrl(urlStr));

        log.info("====id2mid,id:[{}],mid:[{}]", idStr, id2mid(idStr));
        log.info("====mid2id,mid:[{}],id:[{}]", midStr, mid2id(midStr));
        // getMurlBuMid
        // getMurlById
    }
}
