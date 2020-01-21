package org.playpi.study.javabug;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Java 精度损失示例
 */
@Slf4j
public class LossPrecision {

    @Test
    public void lossPrecisionTest() {
        Double d1 = 0.3D;
        Double d2 = 0.03D;
        // 结果不等于0.33
        log.info("====sum:[{}]", d1 + d2);
    }

}
