package org.playpi.study.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * HTTP 工具类
 */
@Slf4j
public class HttpUtil {

    public static final String UA_PC_CHROME = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/36.0.1985.125 Safari/537.36";
    private static final int SOCK_TIMEOUT = 20000;
    private static final int CONNECT_TIMEOUT = 20000;

    /**
     * 标记方法类型
     */
    public enum HTTP_METHOD {
        GET, POST, DELETE
    }

    /**
     * 根据参数发送http请求
     * 获取结果
     *
     * @param url
     * @param headers
     * @param method
     * @param useSsl
     * @return
     */
    public static String getHttpResult(String url, Map<String, String> headers, HTTP_METHOD method, boolean useSsl) {
        String resultStr = null;
        HttpClient httpclient = new DefaultHttpClient();
        if (useSsl) {
            httpclient = wrapClient(httpclient);
        }
        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONNECT_TIMEOUT); // 连接超时10s
        httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, SOCK_TIMEOUT);
        if (useSsl) {
            httpclient.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, false);
        }
        HttpRequestBase httpRequestBase = new HttpGet(url);
        switch (method) {
            case GET:
                break;
            case POST:
                httpRequestBase = new HttpPost(url);
                break;
            case DELETE:
                httpRequestBase = new HttpDelete(url);
                break;
            default:
                break;
        }
        httpRequestBase.setHeader("User-Agent", UA_PC_CHROME);
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpRequestBase.setHeader(entry.getKey(), entry.getValue());
            }
        }
        // 重试5次
        int retry = 5;
        while (0 < retry--) {
            try {
                HttpResponse response = httpclient.execute(httpRequestBase);
                HttpEntity entity = response.getEntity();
                String html = EntityUtils.toString(entity);
                resultStr = html;
                break;
            } catch (Exception e) {
                log.error("!!!!请求出错,重试:" + e.getMessage(), e);
            }
        }
        httpclient.getConnectionManager().shutdown();
        return resultStr;
    }

    /**
     * 构造支持 ssl 协议的 client
     *
     * @param base
     * @return
     */
    public static HttpClient wrapClient(HttpClient base) {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {

                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            X509HostnameVerifier verifier = new X509HostnameVerifier() {

                public void verify(String string, SSLSocket ssls) throws IOException {
                }

                public void verify(String s, X509Certificate x509Certificate) throws SSLException {

                }

                public void verify(String string, String[] strings, String[] strings1) throws SSLException {
                }

                public boolean verify(String string, SSLSession ssls) {
                    return true;
                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx);
            ssf.setHostnameVerifier(verifier);
            ClientConnectionManager ccm = base.getConnectionManager();
            SchemeRegistry sr = ccm.getSchemeRegistry();
            sr.register(new Scheme("https", ssf, 443));
            return new DefaultHttpClient(ccm, base.getParams());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
