package org.playpi.study.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class TestEsClusterUtil {

    @Test
    public void testCloseIndex() {
        String host = "dev1:9200";
        String indexName = "test-index-v1";
        log.info("====result:[{}]", EsClusterUtil.closeIndex(host, indexName, false));
    }

}
