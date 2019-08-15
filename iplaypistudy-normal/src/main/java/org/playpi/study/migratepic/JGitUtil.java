package org.playpi.study.migratepic;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Git操作工具类
 */
@Slf4j
public class JGitUtil {

    private static String LOCAL_REPOGIT_CONFIG = "E:\\img\\img-playpi\\.git";
    private static String GIT_USERNAME = "username";
    private static String GIT_PASSWORD = "password";

    public static void main(String[] args) {
    }

    /**
     * 提交并推送代码至远程服务器
     *
     * @param desc 提交描述
     * @return
     */
    public static boolean commitAndPush(String desc) {
        boolean commitAndPushFlag = false;
        try (Git git = Git.open(new File(LOCAL_REPOGIT_CONFIG))) {
            UsernamePasswordCredentialsProvider provider = new UsernamePasswordCredentialsProvider(GIT_USERNAME, GIT_PASSWORD);
            git.add().addFilepattern(".").call();
            // 提交
            git.commit().setMessage(desc).call();
            // 推送到远程,不报错默认为成功
            git.push().setCredentialsProvider(provider).call();
            commitAndPushFlag = true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Commit And Push error!" + e.getMessage());
        }
        return commitAndPushFlag;
    }
}