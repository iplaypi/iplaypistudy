package org.playpi.study.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * 大数值计算工具类
 *
 * @see #add(double, double, int, int)
 * @see #subtract(double, double, int, int)
 * @see #multiply(double, double, int, int)
 * @see #div(double, double)
 * @see #div(double, double)
 * @see #round(double, int, int)
 */
@Slf4j
public class BigDecimalUtil {

    // 默认运算结果的精度
    private static final int SCALE_DEFAULT_INT = 2;
    // 默认的取舍模式,四舍五入
    private static final int ROUNDING_MODE_DEFAULT_INT = BigDecimal.ROUND_HALF_UP;

    // 不允许实例化(单元测试需要public,先关闭)
/*    private BigDecimalUtil() {
    }*/

    /**
     * 加法运算
     *
     * @param v1
     * @param v2
     * @param scale        精度
     * @param roundingMode 取舍模式,示例:BigDecimal.ROUND_HALF_UP
     * @return
     */
    public static double add(double v1, double v2, int scale, int roundingMode) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).setScale(scale, roundingMode).doubleValue();
    }

    /**
     * 减法运算
     *
     * @param v1
     * @param v2
     * @param scale        精度
     * @param roundingMode 取舍模式,示例:BigDecimal.ROUND_HALF_UP
     * @return
     */
    public static double subtract(double v1, double v2, int scale, int roundingMode) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).setScale(scale, roundingMode).doubleValue();
    }

    /**
     * 乘法运算
     *
     * @param v1
     * @param v2
     * @param scale        精度
     * @param roundingMode 取舍模式,示例:BigDecimal.ROUND_HALF_UP
     * @return
     */
    public static double multiply(double v1, double v2, int scale, int roundingMode) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).setScale(scale, roundingMode).doubleValue();
    }

    /**
     * 除法运算(除法的场景容易有很多位小数的情况)
     * 相对精确,当发生除不尽的情况时,保留2位小数,四舍五入
     *
     * @param v1
     * @param v2
     * @return
     */
    public static double div(double v1, double v2) {
        return div(v1, v2, SCALE_DEFAULT_INT, ROUNDING_MODE_DEFAULT_INT);
    }

    /**
     * 除法运算(除法的场景容易有很多位小数的情况)
     * 相对精确,当发生除不尽的情况时,由scale参数指定精度,由roundingMode决定取舍方式
     *
     * @param v1
     * @param v2
     * @param scale        精度
     * @param roundingMode 取舍模式,示例:BigDecimal.ROUND_HALF_UP
     * @return
     */
    public static double div(double v1, double v2, int scale, int roundingMode) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, roundingMode).doubleValue();
    }

    /**
     * 精确的四舍五入处理
     *
     * @param v
     * @param scale        精度
     * @param roundingMode 取舍模式,示例:BigDecimal.ROUND_HALF_UP
     * @return
     */
    public static double round(double v, int scale, int roundingMode) {
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, roundingMode).doubleValue();
    }

    /**
     * 单元测试,保留2位小数,采用四舍五入的方式
     */
    @Test
    public void bigDecimalUtilTest() {
        // 结果等于0.06
        log.info("====add:[{}]", add(0.05, 0.01, SCALE_DEFAULT_INT, ROUNDING_MODE_DEFAULT_INT));
        // 结果等于0.58
        log.info("====subtract:[{}]", subtract(1, 0.42, SCALE_DEFAULT_INT, ROUNDING_MODE_DEFAULT_INT));
        // 结果等于401.5
        log.info("====multiply:[{}]", multiply(4.015, 100, SCALE_DEFAULT_INT, ROUNDING_MODE_DEFAULT_INT));
        // 结果等于1.23
        log.info("====div:[{}]", div(123.3, 100));
        // 结果等于1.233(除法得到的结果值1.23300)
        log.info("====round:[{}]", round(div(123.3, 100, 5, ROUNDING_MODE_DEFAULT_INT), 3, ROUNDING_MODE_DEFAULT_INT));
    }
}
