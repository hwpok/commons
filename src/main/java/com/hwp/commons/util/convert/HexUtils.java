package com.hwp.commons.util.convert;

/**
 * 十六进制编码工具类
 * Hex放大2倍, Base64是1.33倍
 * @author wanpeng.hui
 * @since 2018-11-28
 */
public final class HexUtils {

    private HexUtils() {
    }

    /**
     * 字节数组转十六进制字符串
     *
     * @param bytes 字节数组
     * @return 十六进制字符串（小写）
     * @throws IllegalArgumentException 如果bytes为null
     */
    public static String encode(byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            hex.append(String.format("%02x", b & 0xFF));
        }
        return hex.toString();
    }

    /**
     * 十六进制字符串转字节数组
     *
     * @param hex 十六进制字符串
     * @return 字节数组
     * @throws IllegalArgumentException 如果hex为null或长度为奇数
     */
    public static byte[] decode(String hex) {
        if (hex == null) {
            return null;
        }
        if (hex.length() % 2 != 0) {
            return null;
        }

        byte[] bytes = new byte[hex.length() / 2];
        try {
            for (int i = 0; i < hex.length(); i += 2) {
                bytes[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
            }
            return bytes;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 字节数组转十六进制字符串（大写）
     *
     * @param bytes 字节数组
     * @return 十六进制字符串（大写）
     */
    public static String encodeUpper(byte[] bytes) {
        return encode(bytes).toUpperCase();
    }

    /**
     * 检查字符串是否为有效的十六进制格式
     *
     * @param hex 待检查的字符串
     * @return 是否为有效的十六进制字符串
     */
    public static boolean isValidHex(String hex) {
        if (hex == null || hex.length() % 2 != 0) {
            return false;
        }
        return hex.matches("[0-9a-fA-F]+");
    }
}
