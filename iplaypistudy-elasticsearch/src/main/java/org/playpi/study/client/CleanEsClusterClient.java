package org.playpi.study.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.playpi.study.base.ClientRunner;
import org.playpi.study.base.InitRunner;
import org.playpi.study.util.EsClusterUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 清理es集群的工具
 * 关闭索引/开启索引/删除索引
 * <p>
 * 执行有风险,操作需谨慎
 * <p>
 * sh run.sh cleanes -in ./xx -type close
 */
@Slf4j
public class CleanEsClusterClient extends ClientRunner {

    /**
     * 跑程序
     *
     * @param args
     */
    public static void main(String[] args) {
        log.info("====跑数入口");
        String className = CleanEsClusterClient.class.getName();
        try {
            log.info("====跑数初始化");
            Class clazz = Class.forName(className);
            ClientRunner ClientRunner = (ClientRunner) clazz.getConstructor().newInstance();
            log.info("====跑数开始执行:{}", clazz.getSimpleName());
            InitRunner.initRunner(args, clazz.getSimpleName(), ClientRunner);
            log.info("====跑数执行完成,是否成功请查看日志");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("!!!!跑数无法找到主类或实例化主类失败或者执行出错:{}", className);
            log.error("!!!!跑数ERROR: " + e.getMessage(), e);
        }
    }

    @Override
    public Options initOptions() {
        Options options = new Options();
        options.addOption("in", true, "输入文件,里面存放索引名字,一行一个");
        options.addOption("type", true, "执行操作,delete-删除,close-关闭,open-开启");
        return options;
    }

    @Override
    public boolean validateOptions(CommandLine cmdLine) {
        return cmdLine.hasOption("in") && cmdLine.hasOption("type");
    }

    @Override
    public void start(CommandLine cmdLine) {
        log.info("====跑数入口接收到的参数信息: {}", getCmdValues(cmdLine));
        String in = cmdLine.getOptionValue("in");
        String type = cmdLine.getOptionValue("type");
        String host = "dev1:9200";
        AtomicInteger total = new AtomicInteger(0);
        try {
            List<String> lines = FileUtils.readLines(new File(in));
            log.info("====读取文件内容行数:[{}],示例:[{}]", lines.size(), lines.get(0));
            lines.forEach(line -> {
                switch (type) {
                    case "delete":
                        if (EsClusterUtil.deleteIndex(host, line, false)) {
                            total.addAndGet(1);
                        }
                        break;
                    case "open":
                        if (EsClusterUtil.openIndex(host, line, false)) {
                            total.addAndGet(1);
                        }
                        break;
                    case "close":
                        if (EsClusterUtil.closeIndex(host, line, false)) {
                            total.addAndGet(1);
                        }
                        break;
                }
                log.info("====操作1个索引完成:[{}]", line);
            });
            log.info("====索引个数:[{}],执行操作:[{}],执行成功个数:[{}]", lines.size(), type, total.get());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
