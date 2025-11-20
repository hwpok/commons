package com.hwp.commons.util.convert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * 类型转换工具类
 * @author wanpeng.hui
 * @since 2018-11-28
 */
@SuppressWarnings("unused")
public final class ConvertUtils {

    private ConvertUtils() {}

    /**
     * 安全地将任意对象转换为字符串
     */
    public static String safeToString(Object obj, String defaultValue) {
        if (obj == null) return defaultValue;

        if (obj instanceof Number) {
            if (obj instanceof Float || obj instanceof Double) {
                return FormatUtils.formatNumber((Number) obj);
            } else if (obj instanceof BigDecimal bigDecimal) {
                return bigDecimal.setScale(4, RoundingMode.HALF_UP)
                        .stripTrailingZeros()
                        .toPlainString();
            } else {
                return obj.toString();
            }
        } else if (obj instanceof Date) {
            return String.valueOf(((Date) obj).getTime());
        } else {
            return obj.toString();
        }
    }

    public static String safeToString(Object obj) {
        return safeToString(obj, "");
    }

    /**
     * 将字符串安全转换为目标类型
     */
    @SuppressWarnings("unchecked")
    public static <T> T parseToType(String str, Class<T> targetType, T defaultValue) {
        if (StringUtils.isBlank(str)) return defaultValue;

        try {
            if (targetType == Boolean.class || targetType == boolean.class) {
                return (T) Boolean.valueOf(str);
            } else if (targetType == Byte.class || targetType == byte.class) {
                return (T) Byte.valueOf(str);
            } else if (targetType == Short.class || targetType == short.class) {
                return (T) Short.valueOf(str);
            } else if (targetType == Integer.class || targetType == int.class) {
                return (T) Integer.valueOf(str);
            } else if (targetType == Long.class || targetType == long.class) {
                return (T) Long.valueOf(str);
            } else if (targetType == Float.class || targetType == float.class) {
                return (T) Float.valueOf(str);
            } else if (targetType == Double.class || targetType == double.class) {
                return (T) Double.valueOf(str);
            } else if (targetType == String.class) {
                return (T) str;
            } else if (targetType == Date.class) {
                return (T) new Date(Long.parseLong(str));
            } else if (targetType == BigDecimal.class) {
                return (T) new BigDecimal(str);
            } else {
                return defaultValue;
            }
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static <T> T parseToType(String str, Class<T> targetType) {
        return parseToType(str, targetType, null);
    }
}

