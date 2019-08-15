package org.playpi.study.migratepic;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 迁移图床小工具:微博图床到 GitHub
 * <p>
 * 图片链接示例
 * https://raw.githubusercontent.com/iplaypi/img-playpi/master/img/old/20190502183444.png
 * https://ws1.sinaimg.cn/large/b7f2e3a3gy1g2hlkwnfm9j214a0hr75v.jpg
 */
@Slf4j
public class MigratePic {

    public static void main(String[] args) {
//        String dir = "e:\\baktest";
//        String outDir = "e:\\baktest-out";
        String dir = "e:\\bak";
        String outDir = "e:\\bak-out";
        Set<File> fileSet = getAllFiles(dir);
        log.info("====文件个数:" + fileSet.size());
        for (File file : fileSet) {
            try {
                // 1-读取文件,抽取微博图床的链接与图片名称
                String content = FileUtils.readFileToString(file, "utf-8");
                Map<String, String> imgMap = extractImg(content);
                // 2-下载图片并上传至 GitHub
                Map<String, String> urlMap = uploadGithub(imgMap);
                // 3-替换所有链接
                content = replaceUrl(content, urlMap);
                // 4-内容写回新文件
                String outFile = outDir + File.separator + file.getName();
                FileUtils.writeStringToFile(new File(outFile), content, "utf-8");
                log.info("====处理文件完成:{},获取新浪图床链接个数:{},上传 GitHub 个数:{}", file.getAbsolutePath(),
                        imgMap.size(), urlMap.size());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 下载图片到指定的文件目录
     */
    public static boolean downloadImg(String url, String dir, String fileName) {
        boolean isSuc = false;
        HttpClient httpclient = null;
        int retry = 5;
        while (0 < retry--) {
            try {
                httpclient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(url);
                httpget.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.79 Safari/537.1");
                httpget.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                HttpResponse resp = httpclient.execute(httpget);
                if (HttpStatus.SC_OK == resp.getStatusLine().getStatusCode()) {
                    HttpEntity entity = resp.getEntity();
                    InputStream in = entity.getContent();
                    isSuc = savePicToDisk(in, dir, fileName);
                    return isSuc;
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("!!!!下载失败,重试一次");
            } finally {
                httpclient.getConnectionManager().shutdown();
            }
        }
        return isSuc;
    }

    /**
     * 根据输入流,保存内容到指定的目录文件
     *
     * @param in
     * @param dirPath
     * @param filePath
     */
    private static boolean savePicToDisk(InputStream in, String dirPath, String filePath) {
        try {
            File dir = new File(dirPath);
            if (dir == null || !dir.exists()) {
                dir.mkdirs();
            }
            // 拼接文件完整路径
            String realPath = dirPath.concat(filePath);
            File file = new File(realPath);
            if (file == null || !file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = in.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("!!!!写入文件失败");
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private static String githubUrl = "https://raw.githubusercontent.com/iplaypi/img-playpi/master/img/old/";

    /**
     * 提交本地的图片到 GitHub,并拼接新的图片链接
     *
     * @param imgMap
     * @return
     */
    private static Map<String, String> uploadGithub(Map<String, String> imgMap) {
        String imgDir = "E:\\img\\img-playpi\\img\\old\\";
        Map<String, String> urlMap = new HashMap<>();
        for (Map.Entry<String, String> entry : imgMap.entrySet()) {
            String oldUrl = entry.getKey();
            String imgName = entry.getValue();
            boolean isSuc = downloadImg(oldUrl, imgDir, imgName);
            if (isSuc) {
                String newUrl = githubUrl + imgName;
                urlMap.put(oldUrl, newUrl);
            }
        }
        log.info("====开始上传文件到 GitHub, size: {}", urlMap.size());
        // 统一上传到 GitHub,这一步骤可以省略,留到最后手动提交即可
        boolean gitSuc = JGitUtil.commitAndPush("add and commit by Java client,img size: " + urlMap.size());
        if (!gitSuc) {
            urlMap.clear();
        }
        return urlMap;
    }

    private static Pattern PATTERN = Pattern.compile("https://[0-9a-zA-Z]{3}\\.sinaimg\\.cn/large/[0-9a-zA-Z]{8,50}\\.jpg");

    /**
     * 抽取微博图床的图片链接与图片文件名
     *
     * @param string
     * @return
     */
    private static Map<String, String> extractImg(String string) {
        Map<String, String> imgMap = new HashMap<>();
        Matcher matcher = PATTERN.matcher(string);
        while (matcher.find()) {
            String oldUrl = matcher.group();
            int index = oldUrl.lastIndexOf("/");
            if (0 < index) {
                String imgName = oldUrl.substring(index + 1);
                imgMap.put(oldUrl, imgName);
            }
        }
        return imgMap;
    }

    /**
     * 替换所有的图片链接
     *
     * @param string
     * @param urlMap
     * @return
     */
    private static String replaceUrl(String string, Map<String, String> urlMap) {
        for (Map.Entry<String, String> entry : urlMap.entrySet()) {
            String oldUrl = entry.getKey();
            String newUrl = entry.getValue();
            string = string.replaceAll(oldUrl, newUrl);
        }
        return string;
    }

    /**
     * 获取指定文件夹内的所有文件
     *
     * @param dir
     * @return
     */
    private static Set<File> getAllFiles(String dir) {
        Set<File> fileSet = new HashSet<>();
        File file = new File(dir + File.separator);
        for (File textFile : file.listFiles()) {
            fileSet.add(textFile.getAbsoluteFile());
        }
        return fileSet;
    }
}
