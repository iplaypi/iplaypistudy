package org.playpi.study.util;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * es 集群工具类
 * 获取索引信息
 * 关闭索引
 * 删除索引等
 */
@Slf4j
public class EsClusterUtil {

    /**
     * 指定主机的所有索引信息<索引真名,别名>
     * 可能会有几千个
     * 集群比较特殊(所有索引的别名一致),不获取别名了,避免误删
     *
     * @param hostport
     * @param useSsl   是否使用https协议
     * @return
     */
    public static Map<String, String> getAllIndexName(String hostport, boolean useSsl) {
        Map<String, String> result = Maps.newHashMap();
        String url = "http://" + hostport + "/_aliases?pretty=1";
        String resultStr = HttpUtil.getHttpResult(url, null, HttpUtil.HTTP_METHOD.GET, useSsl);
        log.info("====http请求反馈结果:[{}]", resultStr);
        Map<String, Object> tmp = new Gson().fromJson(resultStr, Map.class);
        tmp.entrySet().forEach(entry -> {
            String indexName = entry.getKey();
            String indexAliaseName = "";
            result.put(indexName, indexAliaseName);
        });
        return result;
    }

    /**
     * 关闭指定的索引
     * 索引可以批量传入,使用逗号分隔即可
     *
     * @param hostport
     * @param indexName
     * @param useSsl    是否使用https协议
     * @return
     */
    public static boolean closeIndex(String hostport, String indexName, boolean useSsl) {
        String url = "http://" + hostport + "/" + indexName + "/_close";
        String resultStr = HttpUtil.getHttpResult(url, null, HttpUtil.HTTP_METHOD.POST, useSsl);
        Map<String, Object> resultMap = new Gson().fromJson(resultStr, Map.class);
        if (null != resultMap && Boolean.valueOf(resultMap.getOrDefault("acknowledged", false).toString())) {
            return true;
        }
        return false;
    }

    /**
     * 打开指定的索引
     * 索引可以批量传入,使用逗号分隔即可
     *
     * @param hostport
     * @param indexName
     * @param useSsl    是否使用https协议
     * @return
     */
    public static boolean openIndex(String hostport, String indexName, boolean useSsl) {
        String url = "http://" + hostport + "/" + indexName + "/_open";
        String resultStr = HttpUtil.getHttpResult(url, null, HttpUtil.HTTP_METHOD.POST, useSsl);
        Map<String, Object> resultMap = new Gson().fromJson(resultStr, Map.class);
        if (null != resultMap && Boolean.valueOf(resultMap.getOrDefault("acknowledged", false).toString())) {
            return true;
        }
        return false;
    }

    /**
     * 删除指定的索引
     * 索引可以批量传入,使用逗号分隔即可
     *
     * @param hostport
     * @param indexName
     * @param useSsl    是否使用https协议
     * @return
     */
    public static boolean deleteIndex(String hostport, String indexName, boolean useSsl) {
        String url = "http://" + hostport + "/" + indexName;
        String resultStr = HttpUtil.getHttpResult(url, null, HttpUtil.HTTP_METHOD.DELETE, useSsl);
        Map<String, Object> resultMap = new Gson().fromJson(resultStr, Map.class);
        if (null != resultMap && Boolean.valueOf(resultMap.getOrDefault("acknowledged", false).toString())) {
            return true;
        }
        return false;
    }
}
