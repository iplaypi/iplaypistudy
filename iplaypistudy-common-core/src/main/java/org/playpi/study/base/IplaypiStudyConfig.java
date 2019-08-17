package org.playpi.study.base;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;

import java.io.Serializable;

/**
 * 单例模式
 */
@Slf4j
public class IplaypiStudyConfig extends Configuration implements Serializable {

    private volatile static IplaypiStudyConfig iplaypiStudyConfig;

    /**
     * 加载基本配置文件
     */
    private IplaypiStudyConfig() {
        this.addResource("core-site.xml");
        this.addResource("hbase-site.xml");
        this.addResource("hdfs-site.xml");
        this.addResource("mapred-site.xml");
        this.addResource("yarn-site.xml");
    }

    public static IplaypiStudyConfig getInstance() {
        if (null == iplaypiStudyConfig) {
            synchronized (IplaypiStudyConfig.class) {
                if (null == iplaypiStudyConfig) {
                    iplaypiStudyConfig = new IplaypiStudyConfig();
                }
            }
        }
        iplaypiStudyConfig.set("hbase.client.ipc.pool.type", "RoundRobinPool");
        iplaypiStudyConfig.set("hbase.client.ipc.pool.size", "10");
        iplaypiStudyConfig.set("hbase.rpc.timeout", "900000");
        iplaypiStudyConfig.set("hbase.client.scanner.timeout.period", "900000");
        iplaypiStudyConfig.set("hbase.rpc.shortoperation.timeout", "300000");
        log.info("====加载配置信息完成,生成实例:{}", iplaypiStudyConfig);
        return iplaypiStudyConfig;
    }
}
