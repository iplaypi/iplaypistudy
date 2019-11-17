package org.playpi.study.util;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
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
     * 根据主机端口列表/集群名称,创建es连接
     * 由于开启连接需要占用资源,不要开启过多,并在使用完毕后及时关闭
     *
     * @param hostArr
     * @param clusterName
     * @return
     */
    public static TransportClient initTransportClient(String[] hostArr, String clusterName) {
        TransportClient client = null;
        Settings settings = Settings.builder()
                .put("cluster.name", clusterName)
                .put("client.transport.ping_timeout", "60s")
                .put("client.transport.sniff", true)//开启嗅探特性
                .build();
        /**
         * String[] hostArr = new String[]{"hostname1:port", "hostname2:port", "hostname3:port"};
         */
        TransportAddress[] transportAddresses = new InetSocketTransportAddress[hostArr.length];
        for (int i = 0; i < hostArr.length; i++) {
            String[] parts = hostArr[i].split(":");
            try {
                InetAddress inetAddress = InetAddress.getByName(parts[0]);
                transportAddresses[i] = new InetSocketTransportAddress(inetAddress, Integer.parseInt(parts[1]));
            } catch (UnknownHostException e) {
                log.error("!!!!es连接初始化出错: " + e.getMessage(), e);
                return client;
            }
        }
        client = new PreBuiltTransportClient(settings)
                .addTransportAddresses(transportAddresses);
        return client;
    }

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
