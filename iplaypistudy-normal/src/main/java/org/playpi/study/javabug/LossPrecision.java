package org.playpi.study.javabug;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Java 精度损失示例
 */
@Slf4j
public class LossPrecision {

    @Test
    public void lossPrecisionTest() {
        // 结果不等于0.06
        log.info("====sum:[{}]", 0.05 + 0.01);
        // 结果不等于0.58
        log.info("====sum:[{}]", 1 - 0.42);
        // 结果不等于401.5
        log.info("====sum:[{}]", 4.015 * 100);
        // 结果不等于1.233
        log.info("====sum:[{}]", 123.3 / 100);

        // 1-Math 四舍五入
        double val = 4.015;
        log.info("====round:[{}]", Math.round(val * 100) / 100.0);
        // 2-DecimalFormat格式化,四舍五入,保留2位小数
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        log.info("====format:[{}]", decimalFormat.format(val));
        // 3-BigDecimal,四舍五入,保留2位小数
        BigDecimal bigDecimal1 = new BigDecimal(Double.toString(val));
        BigDecimal bigDecimal2 = new BigDecimal(Double.toString(1D));
        log.info("====multiply:[{}]", bigDecimal1.multiply(bigDecimal2).setScale(2, BigDecimal.ROUND_HALF_UP));
        // 如果直接使用double构造,得到的结果仍旧是错误的
        BigDecimal bigDecimal3 = new BigDecimal(val);
        BigDecimal bigDecimal4 = new BigDecimal(1D);
        log.info("====multiply:[{}]", bigDecimal3.multiply(bigDecimal4).setScale(2, BigDecimal.ROUND_HALF_UP));
    }

}
