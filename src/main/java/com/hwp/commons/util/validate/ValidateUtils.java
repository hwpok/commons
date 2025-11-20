package com.hwp.commons.util.validate;


import com.hwp.commons.enums.ValidationPattern;

/**
 * 参数校验工具类，基于 {@link ValidationPattern} 枚举定义的规则。
 * <p>
 * 所有方法均为 null-safe：当输入为 null 或空字符串时，返回 false。
 * 正则表达式已预编译，性能高效。
 * </p>
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@SuppressWarnings("unused")
public final class ValidateUtils {

    private ValidateUtils() {
        // 工具类禁止实例化
    }

    // ==============================
    // 字符类校验
    // ==============================

    /**
     * 判断字符串是否仅包含大写字母（允许空字符串）。
     */
    public static boolean isUppercaseOnly(String value) {
        return ValidationPattern.UPPER.matches(value);
    }

    /**
     * 判断字符串是否仅包含大写字母和下划线，且以下划线分隔（如 ABC_DEF）。
     */
    public static boolean isUppercaseOrUnderscore(String value) {
        return ValidationPattern.UPPER_UNDERSCORE.matches(value);
    }

    /**
     * 判断字符串是否仅包含小写字母（允许空字符串）。
     */
    public static boolean isLowercaseOnly(String value) {
        return ValidationPattern.LOWER.matches(value);
    }

    /**
     * 判断字符串是否仅包含小写字母和下划线，且以下划线分隔（如 abc_def）。
     */
    public static boolean isLowercaseOrUnderscore(String value) {
        return ValidationPattern.LOWER_UNDERSCORE.matches(value);
    }

    /**
     * 判断字符串是否仅包含英文字母（大小写均可，允许空字符串）。
     */
    public static boolean isAlphaOnly(String value) {
        return ValidationPattern.ALPHA.matches(value);
    }

    /**
     * 判断字符串是否仅包含英文字母和下划线，且以下划线分隔。
     */
    public static boolean isAlphaOrUnderscore(String value) {
        return ValidationPattern.ALPHA_UNDERSCORE.matches(value);
    }

    /**
     * 判断字符串是否仅包含字母和数字（允许空字符串）。
     */
    public static boolean isAlphanumeric(String value) {
        return ValidationPattern.ALPHANUMERIC.matches(value);
    }

    /**
     * 判断字符串是否仅包含字母、数字和下划线，且以下划线分隔。
     */
    public static boolean isAlphanumericOrUnderscore(String value) {
        return ValidationPattern.ALPHANUMERIC_UNDERSCORE.matches(value);
    }

    /**
     * 判断字符串是否仅包含中文汉字（至少一个汉字）。
     */
    public static boolean isChineseOnly(String value) {
        return ValidationPattern.CHINESE.matches(value);
    }

    /**
     * 判断字符串是否仅包含字母、数字和中文汉字。
     */
    public static boolean isAlphanumericOrChinese(String value) {
        return ValidationPattern.ALPHANUMERIC_CHINESE.matches(value);
    }

    /**
     * 判断字符串是否仅包含字母、数字、中文汉字和下划线，且以下划线分隔。
     */
    public static boolean isAlphanumericChineseOrUnderscore(String value) {
        return ValidationPattern.ALPHANUMERIC_CHINESE_UNDERSCORE.matches(value);
    }

    // ==============================
    // 整数长度校验
    // ==============================

    /**
     * 判断是否为 1 位整数（0-9）。
     */
    public static boolean isInt1(String value) {
        return ValidationPattern.INT1.matches(value);
    }

    /**
     * 判断是否为 1~2 位整数（1 到 99）。
     */
    public static boolean isInt2(String value) {
        return ValidationPattern.INT2.matches(value);
    }

    /**
     * 判断是否为 1~4 位整数。
     */
    public static boolean isInt4(String value) {
        return ValidationPattern.INT4.matches(value);
    }

    /**
     * 判断是否为 1~6 位整数。
     */
    public static boolean isInt6(String value) {
        return ValidationPattern.INT6.matches(value);
    }

    /**
     * 判断是否为 1~8 位整数。
     */
    public static boolean isInt8(String value) {
        return ValidationPattern.INT8.matches(value);
    }

    /**
     * 判断是否为 1~10 位整数。
     */
    public static boolean isInt10(String value) {
        return ValidationPattern.INT10.matches(value);
    }

    /**
     * 判断是否为 1~12 位整数。
     */
    public static boolean isInt12(String value) {
        return ValidationPattern.INT12.matches(value);
    }

    // ==============================
    // 金额类
    // ==============================

    /**
     * 判断是否为正数，最多 8 位整数、2 位小数（如 12345678.99）。
     */
    public static boolean isPositiveNumeric8_2(String value) {
        return ValidationPattern.NUMERIC_POS_8_2.matches(value);
    }

    /**
     * 判断是否为带符号数字，最多 8 位整数、2 位小数（如 -123.45）。
     */
    public static boolean isNumeric8_2(String value) {
        return ValidationPattern.NUMERIC_8_2.matches(value);
    }

    // ==============================
    // 网络类
    // ==============================

    /**
     * 判断是否为合法的 IPv4 地址（不支持 IPv6）。
     */
    public static boolean isValidIp(String value) {
        return ValidationPattern.IP.matches(value);
    }

    /**
     * 判断是否为合法的电子邮箱格式。
     */
    public static boolean isValidEmail(String value) {
        return ValidationPattern.EMAIL.matches(value);
    }

    /**
     * 判断是否为中国大陆手机号（11 位，13-19 开头）。
     */
    public static boolean isValidMobile(String value) {
        return ValidationPattern.MOBILE.matches(value);
    }

    /**
     * 判断是否为 HTTP/HTTPS 开头的 URL（简化校验，仅作格式提示）。
     */
    public static boolean isValidUrl(String value) {
        return ValidationPattern.URL.matches(value);
    }

    // ==============================
    // 文件类
    // ==============================

    /**
     * 判断文件名是否为常见图片格式（jpg/jpeg/png/gif/bmp，不区分大小写）。
     */
    public static boolean isImageFile(String filename) {
        return ValidationPattern.IMAGE_FILE.matches(filename);
    }

    /**
     * 判断文件名是否为常见视频格式（mp4/avi/3gp，不区分大小写）。
     */
    public static boolean isVideoFile(String filename) {
        return ValidationPattern.VIDEO_FILE.matches(filename);
    }

    // ==============================
    // 其他常用校验
    // ==============================

    /**
     * 判断是否为合法的中国身份证号（15 位、18 位或末尾 X/x）。
     */
    public static boolean isValidIdCard(String value) {
        return ValidationPattern.ID_CARD.matches(value);
    }

    /**
     * 判断是否为合法用户名：以字母开头，5-18 位，可含字母、数字、下划线。
     */
    public static boolean isValidUsername(String value) {
        return ValidationPattern.USERNAME.matches(value);
    }

    /**
     * 判断是否为合法密码：6-10 位，必须包含大写字母、小写字母和数字。
     */
    public static boolean isValidPassword(String value) {
        return ValidationPattern.PASSWORD.matches(value);
    }

    /**
     * 判断是否为合法 QQ 号：5-10 位数字，首位非 0。
     */
    public static boolean isValidQq(String value) {
        return ValidationPattern.QQ.matches(value);
    }

    /**
     * 判断是否为中国邮政编码：6 位数字，首位非 0。
     */
    public static boolean isValidPostcode(String value) {
        return ValidationPattern.POSTCODE.matches(value);
    }

    // ==============================
    // 带字段名的校验方法（用于抛出带上下文的异常）
    // ==============================

    /**
     * 校验邮箱格式，若不合法则抛出 IllegalArgumentException，包含字段名提示。
     */
    public static void validateEmail(String fieldName, String value) {
        if (!isValidEmail(value)) {
            throw new IllegalArgumentException(ValidationPattern.getHint(fieldName, ValidationPattern.EMAIL));
        }
    }

    /**
     * 校验中国大陆手机号，若不合法则抛出异常。
     */
    public static void validateMobile(String fieldName, String value) {
        if (!isValidMobile(value)) {
            throw new IllegalArgumentException(ValidationPattern.getHint(fieldName, ValidationPattern.MOBILE));
        }
    }

    /**
     * 校验用户名格式，若不合法则抛出异常。
     */
    public static void validateUsername(String fieldName, String value) {
        if (!isValidUsername(value)) {
            throw new IllegalArgumentException(ValidationPattern.getHint(fieldName, ValidationPattern.USERNAME));
        }
    }

    /**
     * 校验密码强度，若不合法则抛出异常。
     */
    public static void validatePassword(String fieldName, String value) {
        if (!isValidPassword(value)) {
            throw new IllegalArgumentException(ValidationPattern.getHint(fieldName, ValidationPattern.PASSWORD));
        }
    }

    /**
     * 校验是否为大写字母，若不合法则抛出异常。
     */
    public static void validateUppercaseOnly(String fieldName, String value) {
        if (!isUppercaseOnly(value)) {
            throw new IllegalArgumentException(ValidationPattern.getHint(fieldName, ValidationPattern.UPPER));
        }
    }
}