package com.hwpok.commons.util.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密工具类
 * <p>
 * 提供常用的哈希算法和HMAC算法实现
 * 支持MD5、SHA系列、HMAC-SHA256等
 * </p>
 *
 * @author wanpeng.hui
 * @since 2018-11-28
 */
@SuppressWarnings("unused")
public final class CryptoUtils {

    private CryptoUtils() {
    }

    /**
     * MD5加密（32位小写）
     *
     * @param source 原字符串
     * @return MD5加密后的32位小写字符串，失败返回空字符串
     */
    public static String md5(String source) {
        return digest(source, "MD5");
    }

    /**
     * MD5加密（32位大写）
     *
     * @param source 原字符串
     * @return MD5加密后的32位大写字符串
     */
    public static String md5Upper(String source) {
        return digest(source, "MD5").toUpperCase();
    }

    /**
     * MD5加密并截取中间16位（8-24位）
     *
     * @param source 原字符串
     * @return MD5中间16位小写字符串
     */
    public static String md5Mid16(String source) {
        String md5 = md5(source);
        return md5.length() >= 24 ? md5.substring(8, 24) : "";
    }

    /**
     * MD5加密并截取中间8位（8-16位）
     *
     * @param source 原字符串
     * @return MD5中间8位大写字符串
     */
    public static String md5Mid8(String source) {
        String md5 = md5(source);
        return md5.length() >= 16 ? md5.substring(8, 16).toUpperCase() : "";
    }

    /**
     * MD5加密并截取中间6位（8-14位）
     *
     * @param source 原字符串
     * @return MD5中间6位大写字符串
     */
    public static String md5Mid6(String source) {
        String md5 = md5(source);
        return md5.length() >= 14 ? md5.substring(8, 14).toUpperCase() : "";
    }

    /**
     * SHA-256加密
     *
     * @param source 原字符串
     * @return SHA-256加密后的64位小写字符串
     */
    public static String sha256(String source) {
        return digest(source, "SHA-256");
    }

    /**
     * SHA-1加密
     *
     * @param source 原字符串
     * @return SHA-1加密后的40位小写字符串
     */
    public static String sha1(String source) {
        return digest(source, "SHA-1");
    }

    /**
     * HMAC-SHA256加密
     *
     * <p>基于密钥的消息认证码，用于确保消息完整性和认证。
     * 常用于API签名、密码存储等场景。</p>
     *
     * @param plainText 明文
     * @param secretKey 密钥
     * @return HMAC-SHA256加密后的64位小写字符串，失败返回空字符串
     */
    public static String hmacSha256(String plainText, String secretKey) {
        if (plainText == null || secretKey == null) {
            return "";
        }

        try {
            String algorithm = "HmacSHA256";
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), algorithm);
            Mac mac = Mac.getInstance(algorithm);
            mac.init(keySpec);

            byte[] macBytes = mac.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(macBytes);

        } catch (Exception e) {
            return "";
        }
    }

    /**
     * HMAC-MD5加密
     *
     * @param plainText 明文
     * @param secretKey 密钥
     * @return HMAC-MD5加密后的32位小写字符串
     */
    public static String hmacMd5(String plainText, String secretKey) {
        if (plainText == null || secretKey == null) {
            return "";
        }

        try {
            String algorithm = "HmacMD5";
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), algorithm);
            Mac mac = Mac.getInstance(algorithm);
            mac.init(keySpec);

            byte[] macBytes = mac.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(macBytes);

        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 通用哈希算法
     *
     * @param source    原字符串
     * @param algorithm 算法名称（MD5、SHA-1、SHA-256等）
     * @return 哈希后的16进制字符串
     */
    private static String digest(String source, String algorithm) {
        if (source == null || source.trim().isEmpty()) {
            return "";
        }

        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] bytes = digest.digest(source.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(bytes);

        } catch (NoSuchAlgorithmException e) {
            // 理论上不会发生，因为都是标准算法
            return "";
        }
    }

    /**
     * 字节数组转16进制字符串
     *
     * @param bytes 字节数组
     * @return 16进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xFF));
        }
        return sb.toString();
    }
}
