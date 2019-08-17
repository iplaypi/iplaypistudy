package org.playpi.study.base;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class TestIplaypiStudyConfig {

    @Test
    public void TestConfit() {
        IplaypiStudyConfig iplaypiStudyConfig = IplaypiStudyConfig.getInstance();
        log.info("====[{}]", iplaypiStudyConfig.get("hbase.zookeeper.quorum"));
    }

}
