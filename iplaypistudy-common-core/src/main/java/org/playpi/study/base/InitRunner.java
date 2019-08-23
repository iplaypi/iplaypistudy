package org.playpi.study.base;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

/**
 * 启动程序专用工具类
 *
 * @author pengfei
 */
@Slf4j
public class InitRunner {

    /**
     * 帮助
     */
    private static final String CLIENT_PARAM_HELP = "help";
    /**
     * 协助打印帮助信息
     */
    private static final HelpFormatter HELP_FORMATTER = new HelpFormatter();

    /**
     * 启动程序
     *
     * @param args         参数
     * @param cmdName      名字
     * @param clientRunner 启动器
     */
    public static void initRunner(String[] args, String cmdName, ClientRunner clientRunner) {
        CommandLineParser parser = new GnuParser();
        Options options = clientRunner.initOptions();
        try {
            CommandLine cmdLine = parser.parse(options, args);
            if (!clientRunner.validateOptions(cmdLine) || cmdLine.hasOption(CLIENT_PARAM_HELP)) {
                HELP_FORMATTER.printHelp(cmdName, options);
                return;
            }
            clientRunner.start(cmdLine);
        } catch (ParseException e) {
            log.error("!!!!Unexpected exception: " + e.getMessage(), e);
            HELP_FORMATTER.printHelp(cmdName, options);
        }
    }
}
