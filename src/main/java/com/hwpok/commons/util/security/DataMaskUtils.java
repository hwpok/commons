package com.hwpok.commons.util.security;

/**
 * 数据脱敏工具类
 *
 * @author wanpeng.hui
 * @since 2018-11-28
 */
@SuppressWarnings("unused")
public final class DataMaskUtils {

    private DataMaskUtils() {
    }

    /**
     * 完全脱敏，所有字符替换为占位符
     *
     * @param obj         原始对象
     * @param placeholder 占位符，默认为 "*"
     * @return 脱敏后的字符串
     */
    public static String mask(Object obj, String placeholder) {
        if (obj == null) {
            return "";
        }

        String ph = placeholder == null ? "*" : placeholder;
        String str = obj.toString();

        return ph.repeat(str.length());
    }

    /**
     * 完全脱敏，使用星号作为占位符
     *
     * @param obj 原始对象
     * @return 脱敏后的字符串
     */
    public static String mask(Object obj) {
        return mask(obj, "*");
    }

    /**
     * 部分脱敏，保留前后几位
     *
     * @param obj         原始对象
     * @param keepStart   前面保留的字符数
     * @param keepEnd     后面保留的字符数
     * @param placeholder 占位符，默认为 "*"
     * @return 脱敏后的字符串
     */
    public static String maskPartial(Object obj, int keepStart, int keepEnd, String placeholder) {
        if (obj == null) {
            return "";
        }

        String ph = placeholder == null ? "*" : placeholder;
        String str = obj.toString();
        int len = str.length();

        // 如果保留的字符数超过总长度，完全脱敏
        if (keepStart + keepEnd >= len) {
            return ph.repeat(len);
        }

        // 前面部分 + 中间脱敏部分 + 后面部分
        String start = str.substring(0, keepStart);
        String middle = ph.repeat(len - keepStart - keepEnd);
        String end = str.substring(len - keepEnd);

        return start + middle + end;
    }

    /**
     * 部分脱敏，使用星号作为占位符
     *
     * @param obj       原始对象
     * @param keepStart 前面保留的字符数
     * @param keepEnd   后面保留的字符数
     * @return 脱敏后的字符串
     */
    public static String maskPartial(Object obj, int keepStart, int keepEnd) {
        return maskPartial(obj, keepStart, keepEnd, "*");
    }
}

