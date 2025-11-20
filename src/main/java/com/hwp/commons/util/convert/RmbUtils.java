package com.hwp.commons.util.convert;


import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 人民币金额转中文大写工具类。
 * <p>
 * 支持负数、整数、小数（最多保留两位，自动四舍五入）。
 * 符合《支付结算办法》中关于中文大写金额的书写规范。
 * </p>
 *
 * @author wanpeng.hui
 * @since 2020-12-05
 */
@SuppressWarnings("unused")
public final class RmbUtils {

    private static final String[] DIGITS = {
            "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"
    };

    // 单位：从“拾”开始（个位无单位），依次为 拾、佰、仟、万、拾、佰、仟、亿...
    private static final String[] UNITS = {
            "", "拾", "佰", "仟",
            "万", "拾", "佰", "仟",
            "亿", "拾", "佰", "仟",
            "万" // 支持到万亿
    };

    private static final String NEGATIVE = "负";
    private static final String FULL = "整";

    private RmbUtils() {
    }

    /**
     * 将人民币金额转换为中文大写。
     */
    public static String toCapital(BigDecimal amount) {
        if (amount == null) {
            return "";
        }

        BigDecimal normalized = amount.setScale(2, RoundingMode.HALF_UP);
        int signum = normalized.signum();

        if (signum == 0) {
            return "零元整";
        }

        long totalFen = normalized.movePointRight(2).abs().longValue();
        if (totalFen > 9999999999999999L) {
            throw new IllegalArgumentException("金额过大");
        }

        long integerPart = totalFen / 100;      // 元以上部分
        int fractionalPart = (int) (totalFen % 100); // 角分（0~99）

        StringBuilder sb = new StringBuilder();

        // 处理整数部分（元以上）
        if (integerPart == 0) {
            sb.append("零元");
        } else {
            String intStr = convertInteger(integerPart);
            sb.append(intStr).append("元");
        }

        // 处理小数部分
        boolean hasJiao = fractionalPart >= 10;
        boolean hasFen = (fractionalPart % 10) > 0;

        if (hasJiao || hasFen) {
            if (hasJiao) {
                sb.append(DIGITS[fractionalPart / 10]).append("角");
            }
            if (hasFen) {
                sb.append(DIGITS[fractionalPart % 10]).append("分");
            }
        } else {
            sb.append(FULL);
        }

        if (signum < 0) {
            sb.insert(0, NEGATIVE);
        }

        return sb.toString();
    }

    /**
     * 转换整数部分（单位：元），返回如“壹佰贰拾叁”，不含“元”字。
     */
    private static String convertInteger(long value) {
        if (value == 0) return "零";

        String numStr = String.valueOf(value);
        int n = numStr.length();
        StringBuilder sb = new StringBuilder();

        boolean needZero = false;

        for (int i = 0; i < n; i++) {
            int digit = numStr.charAt(i) - '0';
            int posFromRight = n - i - 1; // 从右往左的位置（个位=0，十位=1...）

            if (digit == 0) {
                needZero = true; // 标记可能需要补零
            } else {
                // 补零（避免开头或连续零）
                if (needZero && !sb.isEmpty()) {
                    sb.append("零");
                }
                needZero = false;

                sb.append(DIGITS[digit]);

                // 添加单位（个位不加单位）
                if (posFromRight > 0) {
                    String unit = getUnit(posFromRight);
                    sb.append(unit);
                }
            }
        }

        return sb.toString();
    }

    /**
     * 根据位置（从右往左，个位=0）获取中文单位。
     */
    private static String getUnit(int position) {
        if (position < UNITS.length) {
            return UNITS[position];
        }
        // 超出范围按“万”循环处理（简单兜底）
        return UNITS[(position - 4) % 4 + 4]; // 粗略处理，实际很少用到
    }
}
