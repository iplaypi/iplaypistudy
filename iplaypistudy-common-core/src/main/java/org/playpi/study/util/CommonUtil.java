package org.playpi.study.util;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用工具类
 *
 * @see #formatText(String, boolean, boolean, boolean, boolean)
 * 格式化文本内容,去除话题/昵称/链接/表情
 * @see #matchRegexFromText(String, String, boolean)
 * 收集文本中命中指定正则表达式的内容
 * @see #isMatchRegexFromText(String, String)
 * 判断文本是否匹配指定的正则表达式
 */
public class CommonUtil {

    // 微博话题,昵称,链接,表情正则表达式
    private static final String WEIBO_TOPIC = "#[^@<>#\"&'\\r\\n\\t]{1,49}#";
    private static final String WEIBO_NICKNAME = "@[\\u4e00-\\u9fa5A-Z0-9a-z._-]{2,30}";
    private static final String WEIBO_URL = "(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";
    private static final String WEIBO_EMOJI = "\\[[\\u4e00-\\u9fa5A-Za-z]{1,8}\\]";

    private static final Pattern WEIBO_TOPIC_PATTERN = Pattern.compile(WEIBO_TOPIC);
    private static final Pattern WEIBO_NICKNAME_PATTERN = Pattern.compile(WEIBO_NICKNAME);
    private static final Pattern WEIBO_URL_PATTERN = Pattern.compile(WEIBO_URL);
    private static final Pattern WEIBO_EMOJI_PATTERN = Pattern.compile(WEIBO_EMOJI);

    /**
     * 去除文本内容中的
     * 话题(格式#xxx#)
     * 昵称(@xxx)
     * 链接(格式复杂,例如http://xxx,https://xxx,ftp://xxx)
     * emoji表情(格式[xxx])
     *
     * @param text
     * @param rmTopic
     * @param rmNickname
     * @param rmUrl
     * @param rmEmoji
     * @return
     */
    public static String formatText(String text, boolean rmTopic, boolean rmNickname,
                                    boolean rmUrl, boolean rmEmoji) {
        if (StringUtils.isBlank(text)) {
            return "";
        }
        if (rmTopic) {
            Matcher matcher = WEIBO_TOPIC_PATTERN.matcher(text);
            text = matcher.replaceAll(" ").replaceAll("\\s+", " ").trim();
        }
        if (rmNickname) {
            Matcher matcher = WEIBO_NICKNAME_PATTERN.matcher(text);
            text = matcher.replaceAll(" ").replaceAll("\\s+", " ").trim();
        }
        if (rmUrl) {
            Matcher matcher = WEIBO_URL_PATTERN.matcher(text);
            text = matcher.replaceAll(" ").replaceAll("\\s+", " ").trim();
        }
        if (rmEmoji) {
            Matcher matcher = WEIBO_EMOJI_PATTERN.matcher(text);
            text = matcher.replaceAll(" ").replaceAll("\\s+", " ").trim();
        }
        return text;
    }

    /**
     * 从文本中查找指定的正则表达式匹配到的内容
     * 没匹配到内容则返回size为0的Set
     *
     * @param text
     * @param regex
     * @param matchAll 是否匹配到底
     * @return
     */
    public static Set<String> matchRegexFromText(String text, String regex, boolean matchAll) {
        Set<String> result = Sets.newHashSet();
        if (StringUtils.isBlank(text) || StringUtils.isBlank(regex)) {
            return result;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        // 找匹配到的子串,做去重处理
        while (matcher.find()) {
            result.add(matcher.group());
            if (!matchAll) {
                break;
            }
        }
        return result;
    }

    /**
     * 判断文本中是否有满足指定的正则表达式的内容
     *
     * @param text
     * @param regex
     * @return true/false
     */
    public static boolean isMatchRegexFromText(String text, String regex) {
        if (StringUtils.isBlank(text) || StringUtils.isBlank(regex)) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }
}
