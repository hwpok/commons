package com.hwpok.commons.util.security;

import lombok.Getter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * HMAC 签名工具类
 * <p>
 * 签名规则：
 * 1. 过滤掉 value 为 null 或 empty 的参数
 * 2. 按 key 字典序升序排序
 * 3. 拼接为 "key1=value1&key2=value2" 格式（无前导 &）
 * 4. 使用指定 HMAC 算法 + UTF-8 编码进行签名
 * </p>
 *
 * @author wanpeng.hui
 * @since 2020-12-05
 */
@SuppressWarnings("unused")
public final class SignTool {

    private SignTool() {
        // 工具类禁止实例化
    }

    /**
     * 支持的 HMAC 算法
     */
    @Getter
    public enum Algorithm {
        HMAC_SHA1("HmacSHA1"),
        HMAC_SHA256("HmacSHA256"),
        HMAC_SHA512("HmacSHA512");

        private final String name;

        Algorithm(String name) {
            this.name = name;
        }
    }

    // 十六进制字符数组，避免重复创建
    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    /**
     * 使用指定算法对参数进行签名
     *
     * @param params    待签名的参数 map
     * @param secretKey 签名密钥
     * @param algorithm 签名算法
     * @return 小写十六进制签名字符串
     */
    public static String sign(Map<String, String> params, String secretKey, Algorithm algorithm) {
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalArgumentException("Secret key must not be null or empty");
        }
        if (algorithm == null) {
            throw new IllegalArgumentException("Algorithm must not be null");
        }
        if (params == null || params.isEmpty()) {
            return "";
        }

        String canonicalParams = buildCanonicalQueryString(params);
        return computeHmac(canonicalParams, secretKey, algorithm);
    }

    /**
     * 验证签名是否正确
     *
     * @param expectedSignature 期望的签名值
     * @param params            参数 map
     * @param secretKey         密钥
     * @param algorithm         算法
     * @return 签名是否匹配
     */
    public static boolean verify(String expectedSignature, Map<String, String> params,
                                 String secretKey, Algorithm algorithm) {
        if (expectedSignature == null) {
            return false;
        }
        String actualSignature = sign(params, secretKey, algorithm);
        return expectedSignature.equals(actualSignature);
    }

    /**
     * 构建规范化的查询字符串（按 key 字典序排序，跳过 null/empty value）
     */
    private static String buildCanonicalQueryString(Map<String, String> params) {
        return params.entrySet().stream()
                .filter(entry -> entry.getKey() != null && entry.getValue() != null && !entry.getValue().isEmpty())
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
    }

    /**
     * 计算 HMAC 签名
     */
    private static String computeHmac(String data, String secretKey, Algorithm algorithm) {
        try {
            Mac mac = Mac.getInstance(algorithm.getName());
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    secretKey.getBytes(StandardCharsets.UTF_8),
                    algorithm.getName()
            );
            mac.init(secretKeySpec);
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Failed to compute " + algorithm.getName() + " signature", e);
        }
    }

    /**
     * 将字节数组转为小写十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_CHARS[v >>> 4];
            hexChars[i * 2 + 1] = HEX_CHARS[v & 0x0F];
        }
        return new String(hexChars);
    }
}

