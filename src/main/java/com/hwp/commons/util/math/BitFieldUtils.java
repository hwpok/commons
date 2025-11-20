package com.hwp.commons.util.math;

/**
 * 64位位字段（Bit Fields）工具类
 * 用于在 long 中读写连续的位段（如二进制协议、寄存器、紧凑状态等）
 * <p>
 * - 所有位位置从 0 开始（0 = 最低位 LSB）
 * - 所有字段解释为无符号值
 * - 支持 1~64 位宽的字段操作
 *
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@SuppressWarnings("unused")
public final class BitFieldUtils {

    private BitFieldUtils() {
    }

    /**
     * 从源值中提取指定位段的值（无符号）
     *
     * @param source    源值
     * @param position  起始位（0 ~ 63）
     * @param bitCount  位宽（1 ~ 64）
     * @return 提取的字段值（始终非负）
     * @throws IllegalArgumentException 如果 position 或 bitCount 越界
     */
    public static long extract(long source, int position, int bitCount) {
        validate(position, bitCount);
        long mask = (bitCount == 64) ? -1L : ((1L << bitCount) - 1);
        return (source >>> position) & mask;
    }

    /**
     * 在目标值中插入（覆盖写入）指定位段的值
     *
     * @param target    目标值
     * @param value     要写入的值（必须能用 bitCount 位表示）
     * @param position  起始位（0 ~ 63）
     * @param bitCount  位宽（1 ~ 64）
     * @return 写入后的结果
     * @throws IllegalArgumentException 如果参数越界或 value 超出位宽
     */
    public static long insert(long target, long value, int position, int bitCount) {
        validate(position, bitCount);
        long mask = (bitCount == 64) ? -1L : ((1L << bitCount) - 1);
        if ((value & ~mask) != 0) {
            throw new IllegalArgumentException(
                    String.format("value 0x%016x exceeds %d-bit field width", value, bitCount)
            );
        }
        long fieldMask = mask << position;
        return (target & ~fieldMask) | ((value & mask) << position);
    }

    /**
     * 将值打包为仅包含该字段的 long（其余位为0），便于后续组合
     *
     * @param value     字段值
     * @param position  起始位
     * @param bitCount  位宽
     * @return 仅含该字段的值
     * @throws IllegalArgumentException 如果参数越界或 value 超出位宽
     */
    public static long pack(long value, int position, int bitCount) {
        validate(position, bitCount);
        long mask = (bitCount == 64) ? -1L : ((1L << bitCount) - 1);
        if ((value & ~mask) != 0) {
            throw new IllegalArgumentException(
                    String.format("value 0x%016x exceeds %d-bit field width", value, bitCount)
            );
        }
        return (value & mask) << position;
    }

    /**
     * 获取指定位置的 bit 值（0 或 1）
     *
     * @param value    源值
     * @param position 位位置（0 ~ 63）
     * @return 0 或 1
     */
    public static int getBit(long value, int position) {
        if (position < 0 || position >= 64) {
            throw new IllegalArgumentException("position must be in [0, 63]");
        }
        return (int) ((value >>> position) & 1L);
    }

    /**
     * 设置指定位置的 bit 值
     *
     * @param value     源值
     * @param position  位位置（0 ~ 63）
     * @param bitValue  true 表示设为 1，false 表示设为 0
     * @return 设置后的值
     */
    public static long setBit(long value, int position, boolean bitValue) {
        if (position < 0 || position >= 64) {
            throw new IllegalArgumentException("position must be in [0, 63]");
        }
        if (bitValue) {
            return value | (1L << position);
        } else {
            return value & ~(1L << position);
        }
    }

    private static void validate(int position, int bitCount) {
        if (bitCount <= 0 || bitCount > 64) {
            throw new IllegalArgumentException("bitCount must be between 1 and 64");
        }
        if (position < 0 || position + bitCount > 64) {
            throw new IllegalArgumentException("position + bitCount must not exceed 64");
        }
    }
}