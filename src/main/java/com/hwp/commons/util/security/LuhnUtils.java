package com.hwp.commons.util.security;

/**
 * Luhn 算法工具类（用于银行卡号、身份证号等校验码验证与生成）。
 * <p>
 * 算法说明：从右向左，偶数位（第2、4、6...位）数字 ×2，若结果 >9 则减9，
 * 所有位求和后能被10整除则合法。
 *
 * @author wanpeng.hui
 */
@SuppressWarnings("unused")
public final class LuhnUtils {

    private LuhnUtils() {
    }

    /**
     * 校验给定字符串是否符合 Luhn 算法（如银行卡号）。
     *
     * @param cardNumber 待校验的数字字符串（可含空格）
     * @return 合法则返回 true，否则 false（包括 null、非数字等情况）
     */
    public static boolean isValid(String cardNumber) {
        if (cardNumber == null) {
            return false;
        }
        String clean = cardNumber.replaceAll("\\s+", "");
        if (clean.isEmpty() || !clean.matches("\\d+")) {
            return false;
        }

        int sum = 0;
        boolean doubleDigit = false; // 从右往左，是否需要对当前位 ×2

        // 从最后一位开始向前遍历
        for (int i = clean.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(clean.charAt(i));

            if (doubleDigit) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }

            sum += digit;
            doubleDigit = !doubleDigit; // 下一位切换状态
        }

        return sum % 10 == 0;
    }

    /**
     * 为部分号码生成 Luhn 校验位，并拼接返回完整号码。
     *
     * @param partialNumber 不含校验位的数字字符串（可含空格）
     * @return 完整号码（原字符串 + 校验位），失败返回 null
     */
    public static String generateWithCheckDigit(String partialNumber) {
        if (partialNumber == null) {
            return null;
        }
        String clean = partialNumber.replaceAll("\\s+", "");
        if (clean.isEmpty() || !clean.matches("\\d+")) {
            return null;
        }

        int sum = 0;
        boolean doubleDigit = true; // 因为后面要加校验位，所以当前最后一位是“偶数位”，需×2

        // 从最后一位开始向前遍历（全部都要参与计算）
        for (int i = clean.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(clean.charAt(i));

            if (doubleDigit) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }

            sum += digit;
            doubleDigit = !doubleDigit;
        }

        // 计算校验位：使得 (sum + checkDigit) % 10 == 0
        int checkDigit = (10 - (sum % 10)) % 10;
        return clean + checkDigit;
    }
}