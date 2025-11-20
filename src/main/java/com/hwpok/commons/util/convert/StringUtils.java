package com.hwpok.commons.util.convert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串基础操作工具类
 *
 * <p>提供字符串的空值判断、去重、截取、命名转换等基础操作。
 * 所有方法均为静态方法，线程安全。</p>
 *
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@SuppressWarnings("unused")
public final class StringUtils {

    /** 下划线转驼峰正则表达式 */
    private static final Pattern LINE_PATTERN = Pattern.compile("_(\\w)");
    /** 驼峰转下划线正则表达式 */
    private static final Pattern HUMP_PATTERN = Pattern.compile("[A-Z]");

    private StringUtils() {}

    /**
     * 判断字符串是否为空（null或""）
     *
     * @param str 待检查字符串
     * @return true-为空，false-非空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 判断字符串是否为空白（null、""或仅包含空白字符）
     *
     * @param str 待检查字符串
     * @return true-为空白，false-非空白
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 判断字符串是否非空
     *
     * @param str 待检查字符串
     * @return true-非空，false-为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 判断字符串是否非空白
     *
     * @param str 待检查字符串
     * @return true-非空白，false-为空白
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 去除字符串两端空白，null返回空字符串
     *
     * @param str 原字符串
     * @return 去除空白后的字符串
     */
    public static String trim(String str) {
        return str == null ? "" : str.trim();
    }

    /**
     * 去除字符串两端空白，若结果为空则返回null
     *
     * @param str 原字符串
     * @return 去除空白后的字符串，空则返回null
     */
    public static String trimToNull(String str) {
        if (str == null) return null;
        String trimmed = str.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /**
     * 去除字符串两端空白，若为空则转为HTML空格符
     *
     * @param str 原字符串
     * @return 处理后的字符串
     */
    public static String trimToHtmlSpace(String str) {
        if (str == null) return "&nbsp;";
        String trimmed = str.trim();
        return trimmed.isEmpty() ? "&nbsp;" : trimmed;
    }

    /**
     * 用CDATA包裹字符串（用于XML）
     *
     * @param str 原字符串
     * @return CDATA包裹的字符串
     */
    public static String wrapCData(String str) {
        String content = str == null ? "" : str.trim();
        return "<![CDATA[" + content + "]]>";
    }

    /**
     * 字符串为空白时返回默认值
     *
     * @param str 原字符串
     * @param defaultValue 默认值
     * @return 非空白返回原字符串，否则返回默认值
     */
    public static String defaultIfBlank(String str, String defaultValue) {
        return isBlank(str) ? defaultValue : str.trim();
    }

    /**
     * 安全比较两个对象是否相等（null安全）
     *
     * @param obj1 对象1
     * @param obj2 对象2
     * @return true-相等，false-不相等
     */
    public static boolean equals(Object obj1, Object obj2) {
        return obj1 == obj2 || (obj1 != null && obj1.equals(obj2));
    }

    /**
     * 安全比较两个对象是否不相等
     *
     * @param obj1 对象1
     * @param obj2 对象2
     * @return true-不相等，false-相等
     */
    public static boolean notEquals(Object obj1, Object obj2) {
        return !equals(obj1, obj2);
    }

    /**
     * 按固定长度分割字符串
     *
     * @param str 原字符串
     * @param subSize 每段长度（必须>0）
     * @return 分割后的字符串数组
     */
    public static String[] splitByLength(String str, int subSize) {
        if (str == null || subSize <= 0) return null;
        if (str.length() <= subSize) return new String[]{str};

        int chunkCount = (str.length() + subSize - 1) / subSize;
        String[] result = new String[chunkCount];
        for (int i = 0; i < chunkCount; i++) {
            int start = i * subSize;
            int end = Math.min(start + subSize, str.length());
            result[i] = str.substring(start, end);
        }
        return result;
    }

    /**
     * 截取字符串至最大长度并去除两端空白
     *
     * @param str 原字符串
     * @param maxLength 最大长度
     * @return 处理后的字符串
     */
    public static String trimAndTruncate(String str, int maxLength) {
        if (str == null || maxLength <= 0) return "";
        String trimmed = str.trim();
        return trimmed.length() <= maxLength ? trimmed : trimmed.substring(0, maxLength);
    }

    /**
     * 清除字符串中所有空白字符
     *
     * @param str 原字符串
     * @return 清除空白后的字符串
     */
    public static String removeAllWhitespace(String str) {
        return str == null ? "" : str.replaceAll("\\s+", "");
    }

    /**
     * 判断字符串长度是否在指定范围内
     *
     * @param str 待检查字符串
     * @param min 最小长度（包含）
     * @param max 最大长度（包含）
     * @return true-在范围内，false-不在范围内
     */
    public static boolean isLengthInRange(String str, int min, int max) {
        if (str == null) return false;
        int len = str.length();
        return len >= min && len <= max;
    }

    /**
     * 下划线命名转驼峰命名
     *
     * <p>示例：user_name → userName</p>
     *
     * @param str 下划线命名字符串
     * @return 驼峰命名字符串
     */
    public static String toCamelCase(String str) {
        if (isBlank(str)) return "";
        str = str.toLowerCase();
        Matcher matcher = LINE_PATTERN.matcher(str);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 驼峰命名转下划线命名
     *
     * <p>示例：userName → user_name</p>
     *
     * @param str 驼峰命名字符串
     * @return 下划线命名字符串
     */
    public static String toUnderScoreCase(String str) {
        if (isBlank(str)) return "";
        Matcher matcher = HUMP_PATTERN.matcher(str);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        String result = sb.toString();
        return result.startsWith("_") ? result.substring(1) : result;
    }
}
