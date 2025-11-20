package com.hwp.commons.util.time;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

/**
 * 时间工具类
 *
 * <p>基于Java 8时间API（java.time）提供时间处理功能。
 * 推荐使用LocalDateTime、LocalDate等新API，避免使用java.util.Date。
 * 所有方法均为静态方法，线程安全。</p>
 * <p>统一错误处理策略：
 * <ul>
 *   <li>返回字符串的方法：null参数返回空字符串""</li>
 *   <li>返回对象的方法：null参数或解析失败返回null</li>
 *   <li>返回数值的方法：null参数返回-1</li>
 *   <li>返回boolean的方法：null参数返回true（更安全的默认值）</li>
 * </ul>
 * </p>
 *
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@SuppressWarnings("unused")
public final class DateUtils {

    /**
     * 年份格式：yyyy
     */
    public static final String PATTERN_YEAR = "yyyy";
    /**
     * 日期格式：yyyy-MM-dd
     */
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    /**
     * 日期时间格式：yyyy-MM-dd HH:mm:ss
     */
    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";
    /**
     * 完整格式：yyyy-MM-dd HH:mm:ss.SSS
     */
    public static final String PATTERN_FULL = "yyyy-MM-dd HH:mm:ss.SSS";

    private DateUtils() {
    }

    // ==================== 当前时间 ====================

    /**
     * 获取当前时间戳（毫秒）
     *
     * @return 当前时间的毫秒数
     */
    public static long currentMillis() {
        return System.currentTimeMillis();
    }

    /**
     * 获取当前Unix时间戳（秒）
     *
     * @return Unix时间戳（秒）
     */
    public static long currentUnixTimestamp() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 根据当前时间戳（毫秒）,获取时间
     *
     * @return LocalDateTime对象
     */
    public static LocalDateTime ofEpochMilli(long millis) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(millis),
                ZoneId.of("Asia/Shanghai")
        );
    }

    // ==================== 时间格式化 ====================

    /**
     * 格式化时间
     *
     * <p>支持LocalDateTime、LocalDate、LocalTime等java.time包下的时间类型。
     * null参数返回空字符串</p>
     *
     * @param temporal 时间对象
     * @param pattern  格式模式
     * @return 格式化后的字符串
     */
    public static String format(TemporalAccessor temporal, String pattern) {
        if (temporal == null) {
            return "";
        }
        try {
            return DateTimeFormatter.ofPattern(pattern).format(temporal);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 格式化LocalDateTime
     *
     * @param dateTime 日期时间
     * @param pattern  格式模式
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        return format((TemporalAccessor) dateTime, pattern);
    }

    /**
     * 格式化LocalDate
     *
     * @param date    日期
     * @param pattern 格式模式
     * @return 格式化后的字符串
     */
    public static String format(LocalDate date, String pattern) {
        return format((TemporalAccessor) date, pattern);
    }

    // ==================== 时间解析 ====================

    /**
     * 解析为LocalDateTime
     * <p>解析失败返回null</p>
     *
     * @param text    时间字符串
     * @param pattern 格式模式
     * @return LocalDateTime对象，解析失败返回null
     */
    public static LocalDateTime parseLocalDateTime(String text, String pattern) {
        if (text == null || pattern == null) {
            return null;
        }
        try {
            return LocalDateTime.parse(text, DateTimeFormatter.ofPattern(pattern));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 解析为LocalDate
     * <p>解析失败返回null</p>
     *
     * @param text    时间字符串
     * @param pattern 格式模式
     * @return LocalDate对象，解析失败返回null
     */
    public static LocalDate parseLocalDate(String text, String pattern) {
        if (text == null || pattern == null) {
            return null;
        }
        try {
            return LocalDate.parse(text, DateTimeFormatter.ofPattern(pattern));
        } catch (Exception e) {
            return null;
        }
    }

    // ==================== 时间计算 ====================

    /**
     * 时间加减运算
     *
     * <p>支持天、小时、分钟、秒、毫秒等单位的时间加减。
     * null参数返回null</p>
     *
     * @param dateTime 原始时间
     * @param amount   数量（正数加，负数减）
     * @param unit     时间单位
     * @return 计算后的时间
     */
    public static LocalDateTime add(LocalDateTime dateTime, long amount, ChronoUnit unit) {
        if (dateTime == null) {
            return null;
        }
        try {
            return dateTime.plus(amount, unit);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 增加天数
     *
     * @param dateTime 原始时间
     * @param days     天数
     * @return 计算后的时间
     */
    public static LocalDateTime addDays(LocalDateTime dateTime, int days) {
        return add(dateTime, days, ChronoUnit.DAYS);
    }

    /**
     * 增加小时
     *
     * @param dateTime 原始时间
     * @param hours    小时数
     * @return 计算后的时间
     */
    public static LocalDateTime addHours(LocalDateTime dateTime, long hours) {
        return add(dateTime, hours, ChronoUnit.HOURS);
    }

    // ==================== 时间差计算 ====================

    /**
     * 计算时间差
     *
     * @param start 开始时间
     * @param end   结束时间
     * @param unit  时间单位
     * @return 时间差，参数为null返回-1
     */
    public static long between(Temporal start, Temporal end, ChronoUnit unit) {
        if (start == null || end == null) {
            return -1;
        }
        try {
            return unit.between(start, end);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 计算天数差
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 天数差
     */
    public static long diffInDays(LocalDateTime start, LocalDateTime end) {
        return between(start, end, ChronoUnit.DAYS);
    }

    /**
     * 计算毫秒差
     * <p>参数为null返回-1</p>
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 毫秒差
     */
    public static long diffInMillis(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return -1;
        }
        try {
            return Duration.between(start, end).toMillis();
        } catch (Exception e) {
            return -1;
        }
    }

    // ==================== 时间截断 ====================

    /**
     * 截断时间到指定单位
     *
     * @param dateTime 原始时间
     * @param unit     截断单位
     * @return 截断后的时间，null参数返回null
     */
    public static LocalDateTime truncate(LocalDateTime dateTime, ChronoUnit unit) {
        if (dateTime == null) {
            return null;
        }
        try {
            return dateTime.truncatedTo(unit);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 截断到一天开始（00:00:00）
     * <p>null参数返回null</p>
     *
     * @param dateTime 原始时间
     * @return 当天0点的时间
     */
    public static LocalDateTime truncateToDayStart(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        try {
            return dateTime.toLocalDate().atStartOfDay();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 截断到小时开始（整点）
     *
     * @param dateTime 原始时间
     * @return 整点时间
     */
    public static LocalDateTime truncateToHourStart(LocalDateTime dateTime) {
        return truncate(dateTime, ChronoUnit.HOURS);
    }

    /**
     * 截断到一天结束（23:59:59）
     * <p>null参数返回null</p>
     *
     * @param dateTime 原始时间
     * @return 当天23:59:59的时间
     */
    public static LocalDateTime truncateToDayEnd(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        try {
            return dateTime.toLocalDate().atTime(23, 59, 59);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 截断到一天结束（包含毫秒）
     *
     * <p>返回当天的最后一刻：23:59:59.999999999
     * null参数返回null</p>
     *
     * @param dateTime 原始时间
     * @return 当天最后一刻的时间
     */
    public static LocalDateTime truncateToDayEndWithMax(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        try {
            return dateTime.toLocalDate().atTime(LocalTime.MAX);
        } catch (Exception e) {
            return null;
        }
    }

    // ==================== 过期判断 ====================

    /**
     * 判断是否过期
     *
     * @param expireTime 过期时间
     * @param now        当前时间
     * @return true-已过期，false-未过期，null参数返回true（更安全）
     */
    public static boolean isExpired(Instant expireTime, Instant now) {
        if (expireTime == null || now == null) {
            return true;
        }
        try {
            return expireTime.isBefore(now);
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 判断是否过期（使用当前时间）
     *
     * @param expireTime 过期时间
     * @return true-已过期，false-未过期
     */
    public static boolean isExpired(Instant expireTime) {
        return isExpired(expireTime, Instant.now());
    }

    // ==================== 年月计算 ====================

    /**
     * 年月格式加减月份
     *
     * <p>输入格式：202012（表示2020年12月）
     * 输出格式：202103（表示2021年3月）
     * 无效参数返回0</p>
     *
     * @param yearMonth    年月（格式：yyyyMM）
     * @param offsetMonths 偏移月数（正数加，负数减）
     * @return 计算后的年月
     */
    public static int offsetYearMonth(int yearMonth, int offsetMonths) {
        if (yearMonth < 1000 || yearMonth % 100 > 12 || yearMonth % 100 < 1) {
            return 0;
        }
        try {
            YearMonth ym = YearMonth.of(yearMonth / 100, yearMonth % 100);
            YearMonth result = ym.plusMonths(offsetMonths);
            return result.getYear() * 100 + result.getMonthValue();
        } catch (Exception e) {
            return 0;
        }
    }

    // ==================== 兼容旧API（已废弃） ====================

    /**
     * @deprecated 使用 {@link #format(TemporalAccessor, String)} 替代
     */
    @Deprecated
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        try {
            return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @deprecated 使用 {@link #parseLocalDateTime(String, String)} 替代
     */
    @Deprecated
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        try {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @deprecated 使用 {@link #format(TemporalAccessor, String)} 替代
     */
    @Deprecated
    public static String format(Date date, String pattern) {
        if (date == null || pattern == null) {
            return "";
        }
        try {
            return Instant.ofEpochMilli(date.getTime())
                    .atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern(pattern));
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * @deprecated 使用 {@link #parseLocalDateTime(String, String)} 替代
     */
    @Deprecated
    public static Date parse(String text, String pattern) {
        if (text == null || pattern == null) {
            return null;
        }
        try {
            LocalDateTime ldt = LocalDateTime.parse(text, DateTimeFormatter.ofPattern(pattern));
            return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
            return null;
        }
    }
}
