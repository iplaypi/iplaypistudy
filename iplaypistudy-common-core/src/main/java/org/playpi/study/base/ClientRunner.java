package org.playpi.study.base;

import com.google.common.collect.Maps;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.util.Map;

/**
 * Interface for command-line
 * 用来简化程序的结构
 *
 * @author pengfei
 */
public abstract class ClientRunner {

    /**
     * Initialize the command line options
     *
     * @return command line options
     */
    public abstract Options initOptions();

    /**
     * Validate the input options
     *
     * @param cmdLine
     * @return true if options has been set
     */
    public abstract boolean validateOptions(CommandLine cmdLine);

    /**
     * Start the runner
     *
     * @param cmdLine
     */
    public abstract void start(CommandLine cmdLine);

    /**
     * 获取命令行输入的参数
     *
     * @param commandLine
     * @return
     */
    public static Map<String, String> getCmdValues(CommandLine commandLine) {
        Map<String, String> map = Maps.newHashMap();
        for (Option option : commandLine.getOptions()) {
            map.put(option.getOpt(), option.getValue());
        }
        return map;
    }

}
