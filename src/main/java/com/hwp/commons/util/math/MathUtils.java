package com.hwp.commons.util.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 精确数学计算工具类
 * 解决Java浮点数精度问题，提供金融级精确计算
 *
 * @author wanpeng.hui
 * @since 2018-11-28
 */
public final class MathUtils {

    private MathUtils() {
    }

    /**
     * 精确加法
     */
    public static double add(double v1, double v2) {
        return BigDecimal.valueOf(v1).add(BigDecimal.valueOf(v2)).doubleValue();
    }

    /**
     * 精确减法
     */
    public static double subtract(double v1, double v2) {
        return BigDecimal.valueOf(v1).subtract(BigDecimal.valueOf(v2)).doubleValue();
    }

    /**
     * 精确乘法
     */
    public static double multiply(double v1, double v2) {
        return BigDecimal.valueOf(v1).multiply(BigDecimal.valueOf(v2)).doubleValue();
    }

    /**
     * 精确除法（默认精度10位）
     */
    public static double divide(double v1, double v2) {
        return divide(v1, v2, 10);
    }

    /**
     * 精确除法（指定精度）
     */
    public static double divide(double v1, double v2, int scale) {
        if (v2 == 0) return 0;
        return BigDecimal.valueOf(v1)
                .divide(BigDecimal.valueOf(v2), scale, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /**
     * 四舍五入
     */
    public static double round(double value, int scale) {
        return BigDecimal.valueOf(value)
                .setScale(scale, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /**
     * 四舍五入（字符串输入）
     */
    public static double round(String value, int scale) {
        try {
            return round(new BigDecimal(value).doubleValue(), scale);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 计算百分比：(部分/整体) × 100
     */
    public static double percent(double part, double total, int scale) {
        if (total == 0) return 0;
        return multiply(divide(part, total, scale + 2), 100);
    }

    /**
     * 百分数转小数：除以100
     */
    public static double percentToDecimal(double percent) {
        return divide(percent, 100, 4);
    }

    /**
     * 转换为"万"单位
     */
    public static double toTenThousand(double value, int scale) {
        return divide(value, 10000, scale);
    }

    /**
     * 转换为"千"单位
     */
    public static double toThousand(double value, int scale) {
        return divide(value, 1000, scale);
    }

    /**
     * 精确加法
     */
    public static BigDecimal add(BigDecimal v1, BigDecimal v2) {
        return v1.add(v2);
    }

    /**
     * 精确减法
     */
    public static BigDecimal subtract(BigDecimal v1, BigDecimal v2) {
        return v1.subtract(v2);
    }

    /**
     * 精确乘法
     */
    public static BigDecimal multiply(BigDecimal v1, BigDecimal v2) {
        return v1.multiply(v2);
    }

    /**
     * 精确除法
     */
    public static BigDecimal divide(BigDecimal v1, BigDecimal v2, int scale) {
        if (v2.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return v1.divide(v2, scale, RoundingMode.HALF_UP);
    }

    /**
     * 四舍五入（BigDecimal版本，推荐）
     */
    public static BigDecimal round(BigDecimal value, int scale) {
        return value.setScale(scale, RoundingMode.HALF_UP);
    }
}

