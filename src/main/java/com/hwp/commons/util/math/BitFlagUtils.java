package com.hwp.commons.util.math;

/**
 * 位标志（Bit Flags）工具类
 * 用于组合、检查、设置独立的标志位（每个标志应为 2 的幂）
 *
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@SuppressWarnings("unused")
public final class BitFlagUtils {

    private BitFlagUtils() {
    }

    /**
     * 组合多个标志位（按位或）
     */
    public static int combine(int... flags) {
        int result = 0;
        for (int flag : flags) {
            result |= flag;
        }
        return result;
    }

    /**
     * 检查是否包含指定标志（flag 必须是单一位）
     */
    public static boolean hasFlag(int all, int flag) {
        return (all & flag) == flag;
    }

    /**
     * 检查是否包含所有指定标志
     */
    public static boolean hasAllFlags(int all, int... flags) {
        for (int flag : flags) {
            if (!hasFlag(all, flag)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查是否包含任意指定标志
     */
    public static boolean hasAnyFlag(int all, int... flags) {
        for (int flag : flags) {
            if (hasFlag(all, flag)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设置标志位
     */
    public static int setFlag(int value, int flag) {
        return value | flag;
    }

    /**
     * 清除标志位
     */
    public static int clearFlag(int value, int flag) {
        return value & ~flag;
    }

    /**
     * 切换标志位
     */
    public static int toggleFlag(int value, int flag) {
        return value ^ flag;
    }

    // ================== 实用辅助 ==================

    /**
     * 获取最低位的1所在位置（用于遍历标志）
     */
    public static int getLowestFlagPosition(int value) {
        if (value == 0) return -1;
        return Integer.numberOfTrailingZeros(value);
    }

    /**
     * 计算启用的标志数量
     */
    public static int countFlags(int value) {
        return Integer.bitCount(value);
    }
}