package com.hwp.commons.util.convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * 格式化工具类
 *
 * <p>提供字符串模板替换、数字格式化、字符串连接等功能。
 * 所有方法均为静态方法，线程安全。</p>
 *
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@SuppressWarnings("unused")
public final class FormatUtils {

    /**
     * 浮点数格式化器（保留4位小数）
     */
    private static final DecimalFormat FLOAT_FORMAT = new DecimalFormat("#########.####");
    /**
     * 千分位格式化器模板
     */
    private static final String THOUSAND_PATTERN = "#,###,###,###";
    /**
     * 万分位格式化器模板
     */
    private static final String TEN_THOUSAND_PATTERN = "#,#####,####,####";

    private FormatUtils() {
    }

    /**
     * 简易模板字符串替换
     *
     * <p>使用{}作为占位符，按顺序替换参数。
     * 示例：template("Hello {}, age {}", "Tom", 20) → "Hello Tom, age 20"</p>
     *
     * @param template 模板字符串
     * @param args     替换参数
     * @return 替换后的字符串
     */
    public static String template(String template, Object... args) {
        if (template == null || args == null || args.length == 0) {
            return template == null ? "" : template;
        }

        String result = template;
        for (Object arg : args) {
            int idx = result.indexOf("{}");
            if (idx == -1) break;
            result = result.substring(0, idx) + ConvertUtils.safeToString(arg) + result.substring(idx + 2);
        }
        return result;
    }

    /**
     * 格式化金额显示（保留最多2位小数，去除尾随0）
     *
     * <p>示例：100.50 → "100.5", 100.00 → "100"</p>
     *
     * @param amount 金额数值
     * @return 格式化后的金额字符串
     */
    public static String formatCurrency(double amount) {
        BigDecimal bd = BigDecimal.valueOf(amount)
                .setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros();
        String s = bd.toPlainString();
        return s.endsWith(".") ? s.substring(0, s.length() - 1) : s;
    }

    /**
     * 格式化数字为普通字符串（去除尾随0）
     *
     * @param number       数字对象
     * @param scale        小数位数
     * @param defaultValue 默认值
     * @return 格式化后的字符串
     */
    public static String formatPlain(Number number, int scale, String defaultValue) {
        if (number == null) return defaultValue;

        BigDecimal bd;
        if (number instanceof BigDecimal) {
            bd = (BigDecimal) number;
        } else {
            bd = BigDecimal.valueOf(number.doubleValue());
        }

        return bd.setScale(scale, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }

    /**
     * 格式化数字为普通字符串（默认2位小数）
     *
     * @param number 数字对象
     * @return 格式化后的字符串
     */
    public static String formatPlain(Number number) {
        return formatPlain(number, 2, "");
    }

    /**
     * 格式化数字（保留指定位小数）
     *
     * <p>支持所有Number子类：Byte, Short, Integer, Long, Float, Double, BigDecimal, BigInteger</p>
     *
     * @param number       数字对象
     * @param scale        小数位数
     * @param defaultValue 默认值
     * @return 格式化后的字符串
     */
    public static String formatNumber(Number number, int scale, String defaultValue) {
        if (number == null) return defaultValue;

        BigDecimal bd;

        // 根据具体类型选择最佳转换方式，避免精度丢失
        if (number instanceof BigDecimal) {
            // BigDecimal直接使用，保持精度
            bd = (BigDecimal) number;
        } else if (number instanceof BigInteger) {
            // BigInteger转换为BigDecimal
            bd = new BigDecimal((BigInteger) number);
        } else if (number instanceof Float || number instanceof Double) {
            // 浮点数使用String构造，避免二进制精度问题
            bd = new BigDecimal(number.toString());
        } else {
            // 整数类型使用valueOf，精确转换
            bd = BigDecimal.valueOf(number.longValue());
        }

        // 四舍五入到指定位数
        bd = bd.setScale(scale, RoundingMode.HALF_UP);

        // 使用DecimalFormat格式化
        DecimalFormat df = createDecimalFormat(scale);
        return df.format(bd);
    }


    /**
     * 格式化数字（默认2位小数）
     *
     * @param number 数字对象
     * @return 格式化后的字符串
     */
    public static String formatNumber(Number number) {
        return formatNumber(number, 2, "");
    }

    /**
     * 千分位格式化
     *
     * <p>示例：1234567.89 → "1,234,567.89"</p>
     *
     * @param number 数字
     * @param scale  小数位数
     * @return 千分位格式化字符串
     */
    public static String formatByThousand(Number number, int scale) {
        if (number == null) return "";

        DecimalFormat df = createThousandFormat(scale);
        BigDecimal bd = BigDecimal.valueOf(number.doubleValue())
                .setScale(scale, RoundingMode.HALF_UP);
        return df.format(bd);
    }

    /**
     * 万分位格式化（中国习惯）
     *
     * <p>示例：123456789 → "1,2345,6789"</p>
     *
     * @param number 数字
     * @param scale  小数位数
     * @return 万分位格式化字符串
     */
    public static String formatByTenThousand(Number number, int scale) {
        if (number == null) return "";

        DecimalFormat df = createTenThousandFormat(scale);
        BigDecimal bd = BigDecimal.valueOf(number.doubleValue())
                .setScale(scale, RoundingMode.HALF_UP);
        return df.format(bd);
    }

    /**
     * 按固定长度分割并用空格连接
     *
     * <p>示例：splitAndJoin("12345678", 4) → "1234 5678"</p>
     *
     * @param str     原字符串
     * @param subSize 每段长度
     * @return 分割后用空格连接的字符串
     */
    public static String splitAndJoin(String str, int subSize) {
        if (StringUtils.isBlank(str) || subSize <= 0) return "";

        StringBuilder result = new StringBuilder();
        int length = str.length();

        for (int i = 0; i < length; i += subSize) {
            int end = Math.min(i + subSize, length);
            result.append(str, i, end);
            if (end < length) {
                result.append(" ");
            }
        }

        return result.toString();
    }

    /**
     * 创建指定位数的DecimalFormat
     */
    private static DecimalFormat createDecimalFormat(int scale) {
        StringBuilder pattern = new StringBuilder("##");
        if (scale > 0) {
            pattern.append(".");
            pattern.append("#".repeat(scale));
        }
        return new DecimalFormat(pattern.toString());
    }

    /**
     * 创建千分位DecimalFormat
     */
    private static DecimalFormat createThousandFormat(int scale) {
        StringBuilder pattern = new StringBuilder(THOUSAND_PATTERN);
        if (scale > 0) {
            pattern.append(".");
            pattern.append("#".repeat(scale));
        }
        return new DecimalFormat(pattern.toString());
    }

    /**
     * 创建万分位DecimalFormat
     */
    private static DecimalFormat createTenThousandFormat(int scale) {
        StringBuilder pattern = new StringBuilder(TEN_THOUSAND_PATTERN);
        if (scale > 0) {
            pattern.append(".");
            pattern.append("#".repeat(scale));
        }
        return new DecimalFormat(pattern.toString());
    }
}

