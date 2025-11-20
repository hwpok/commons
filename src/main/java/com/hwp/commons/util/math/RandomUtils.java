package com.hwp.commons.util.math;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机字符/数字生成工具
 *
 * @author wanpeng.hui
 * @since 2020-12-05
 */
@SuppressWarnings("unused")
public final class RandomUtils {

    private static final char[] SYMBOLS = {'!', '@', '#', '$', '%', '&', '*', '-', '_', '=', '+', ':', ',', '?'};

    private RandomUtils() {
    }

    /**
     * 随机生成一个小写字母
     */
    public static String lower() {
        return String.valueOf((char) intRange(97, 122));
    }

    /**
     * 随机生成多个小写字母
     */
    public static String lowers(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(lower());
        }
        return sb.toString();
    }

    /**
     * 随机生成一个大写字母
     */
    public static String upper() {
        return String.valueOf((char) intRange(65, 90));
    }

    /**
     * 随机生成多个大写字母
     */
    public static String uppers(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(upper());
        }
        return sb.toString();
    }

    /**
     * 生成一个大写字母或小写字母
     */
    public static String alpha() {
        return intRange(0, 1) == 0 ? upper() : lower();
    }

    /**
     * 生成多个大写字母或小写字母
     */
    public static String alphas(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(alpha());
        }
        return sb.toString();
    }

    /**
     * 生成大写字母、小写字母或数字
     */
    public static String beta() {
        // 33%概率字母，67%概率数字，更均衡
        int choice = intRange(0, 2);
        if (choice == 0) {
            return upper();
        } else if (choice == 1) {
            return lower();
        } else {
            return String.valueOf(intRange(0, 9));
        }
    }

    /**
     * 生成多个大写字母、小写字母或数字
     */
    public static String betas(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(beta());
        }
        return sb.toString();
    }

    /**
     * 生成大写字母、小写字母、数字或符号
     */
    public static String gamma() {
        int choice = intRange(0, 3);
        return switch (choice) {
            case 0 -> upper();
            case 1 -> lower();
            case 2 -> String.valueOf(intRange(0, 9));
            default -> symbol();
        };
    }

    /**
     * 生成多个大写字母、小写字母、数字或符号
     */
    public static String gammas(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(gamma());
        }
        return sb.toString();
    }

    /**
     * 随机生成指定范围的整数
     */
    public static int intRange(int start, int end) {
        return ThreadLocalRandom.current().nextInt(start, end + 1);
    }

    /**
     * 随机生成0到end的整数
     */
    public static int integer(int end) {
        return intRange(0, end);
    }

    /**
     * 随机生成一个byte数
     */
    public static byte byteValue(int end) {
        return (byte) intRange(0, Math.min(end, 127));
    }

    /**
     * 随机生成指定范围的byte数
     */
    public static byte byteRange(int start, int end) {
        start = Math.max(0, Math.min(start, 127));
        end = Math.max(0, Math.min(end, 127));
        return (byte) intRange(start, end);
    }

    /**
     * 随机生成一个long数
     */
    public static long longValue(int end) {
        return intRange(0, end);
    }

    /**
     * 随机生成指定范围的long数
     */
    public static long longRange(int start, int end) {
        return intRange(start, end);
    }

    /**
     * 随机生成boolean值
     */
    public static boolean bool() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    /**
     * 随机生成指定范围的double数
     */
    public static double doubleRange(int start, int end, int scale) {
        double value = ThreadLocalRandom.current().nextDouble(start, end + 1);
        BigDecimal bd = BigDecimal.valueOf(value);
        return bd.setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 随机生成指定范围的double数（默认2位小数）
     */
    public static double doubleRange(int start, int end) {
        return doubleRange(start, end, 2);
    }

    /**
     * 随机生成指定范围的double字符串
     */
    public static String doubleStrRange(int start, int end, int scale) {
        double value = doubleRange(start, end, scale);
        return String.valueOf(value);
    }

    /**
     * 随机生成指定范围的double字符串（默认2位小数）
     */
    public static String doubleStrRange(int start, int end) {
        return doubleStrRange(start, end, 2);
    }

    /**
     * 获取一个特殊符号
     */
    public static String symbol() {
        return String.valueOf(SYMBOLS[ThreadLocalRandom.current().nextInt(SYMBOLS.length)]);
    }
}

