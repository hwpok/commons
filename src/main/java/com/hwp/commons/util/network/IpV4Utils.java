package com.hwp.commons.util.network;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * IPv4网络工具类
 * <p>
 * 提供IPv4地址与数字的转换、IP范围判断、CIDR网段判断等功能。
 * 适用于IP白名单、网段过滤、IP范围查询等场景。
 * </p>
 *
 * @author wanpeng.hui
 * @since 2018-11-28
 */
@SuppressWarnings("unused")
public final class IpV4Utils {

    /**
     * IPv4地址正则表达式
     */
    private static final Pattern IPV4_PATTERN =
            Pattern.compile("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");

    /**
     * IPv4最大值
     */
    private static final long MAX_IPV4_NUM = 0xFFFFFFFFL;

    private IpV4Utils() {
    }

    /**
     * IPv4字符串转数字
     *
     * @param ip IPv4地址字符串，如 "192.168.1.1"
     * @return 转换后的数字，无效IP返回null
     */
    public static Long toLong(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return null;
        }

        // 快速验证格式
        if (!IPV4_PATTERN.matcher(ip).matches()) {
            return null;
        }

        try {
            String[] segments = ip.split("\\.");
            long result = 0;
            for (int i = 0; i < 4; i++) {
                int segment = Integer.parseInt(segments[i]);
                result |= (long) segment << (24 - i * 8);
            }
            return result;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 数字转IPv4字符串
     *
     * @param ipNum IP数字
     * @return IPv4地址字符串，无效数字返回null
     */
    public static String toString(long ipNum) {
        if (ipNum < 0 || ipNum > MAX_IPV4_NUM) {
            return null;
        }

        return String.format("%d.%d.%d.%d",
                (ipNum >> 24) & 0xFF,
                (ipNum >> 16) & 0xFF,
                (ipNum >> 8) & 0xFF,
                ipNum & 0xFF);
    }

    /**
     * 判断IP数字是否在指定范围内（包含边界）
     *
     * @param startNum 起始IP数字
     * @param endNum   结束IP数字
     * @param testNum  待测试IP数字
     * @return true-在范围内，false-不在或参数无效
     */
    public static boolean isInRange(long startNum, long endNum, long testNum) {
        return startNum <= testNum && testNum <= endNum;
    }

    /**
     * 判断IP是否在指定范围内（包含边界）
     *
     * @param startIp 起始IP字符串
     * @param endIp   结束IP字符串
     * @param testIp  待测试IP字符串
     * @return true-在范围内，false-不在或参数无效
     */
    public static boolean isInRange(String startIp, String endIp, String testIp) {
        Long startNum = toLong(startIp);
        Long endNum = toLong(endIp);
        Long testNum = toLong(testIp);

        if (startNum == null || endNum == null || testNum == null) {
            return false;
        }

        return isInRange(startNum, endNum, testNum);
    }

    /**
     * 判断两个IP是否在同一网段
     *
     * @param ipWithMask 带掩码的IP，如 "192.168.1.0/24"
     * @param testIp     待测试IP
     * @return true-在同一网段，false-不在或参数无效
     */
    public static boolean isSameNetwork(String ipWithMask, String testIp) {
        Objects.requireNonNull(ipWithMask, "IP with mask cannot be null");
        Objects.requireNonNull(testIp, "Test IP cannot be null");

        // 解析CIDR格式
        String[] parts = ipWithMask.split("/");
        if (parts.length != 2) {
            // 如果不是CIDR格式，直接比较IP是否相等
            return ipWithMask.equals(testIp);
        }

        String networkIp = parts[0];
        int maskBits;

        try {
            maskBits = Integer.parseInt(parts[1]);
            if (maskBits < 0 || maskBits > 32) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }

        Long networkNum = toLong(networkIp);
        Long testNum = toLong(testIp);

        if (networkNum == null || testNum == null) {
            return false;
        }

        // 计算子网掩码
        long mask = maskBits == 0 ? 0 : MAX_IPV4_NUM << (32 - maskBits);

        // 比较网络号
        return (networkNum & mask) == (testNum & mask);
    }

    /**
     * 验证IPv4地址格式
     *
     * @param ip IP地址字符串
     * @return true-有效IPv4地址，false-无效
     */
    public static boolean isValid(String ip) {
        return ip != null && IPV4_PATTERN.matcher(ip).matches();
    }

    /**
     * 获取CIDR网段的起始IP
     *
     * @param cidr CIDR格式，如 "192.168.1.0/24"
     * @return 网段起始IP，无效返回null
     */
    public static String getNetworkStart(String cidr) {
        String[] parts = cidr.split("/");
        if (parts.length != 2) {
            return null;
        }

        Long ipNum = toLong(parts[0]);
        if (ipNum == null) {
            return null;
        }

        try {
            int maskBits = Integer.parseInt(parts[1]);
            if (maskBits < 0 || maskBits > 32) {
                return null;
            }

            long mask = maskBits == 0 ? 0 : MAX_IPV4_NUM << (32 - maskBits);
            long startNum = ipNum & mask;

            return toString(startNum);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 获取CIDR网段的结束IP
     *
     * @param cidr CIDR格式，如 "192.168.1.0/24"
     * @return 网段结束IP，无效返回null
     */
    public static String getNetworkEnd(String cidr) {
        String[] parts = cidr.split("/");
        if (parts.length != 2) {
            return null;
        }

        Long ipNum = toLong(parts[0]);
        if (ipNum == null) {
            return null;
        }

        try {
            int maskBits = Integer.parseInt(parts[1]);
            if (maskBits < 0 || maskBits > 32) {
                return null;
            }

            long mask = maskBits == 0 ? 0 : MAX_IPV4_NUM << (32 - maskBits);
            long endNum = (ipNum & mask) | (~mask & MAX_IPV4_NUM);

            return toString(endNum);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

