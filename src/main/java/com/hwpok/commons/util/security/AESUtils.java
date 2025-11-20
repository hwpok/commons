package com.hwpok.commons.util.security;


import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * AES对称加密工具
 *
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@SuppressWarnings("unused")
public final class AESUtils {

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";

    private AESUtils() {
    }

    /**
     * AES加密
     *
     * @param data     待加密数据
     * @param password 加密密码
     * @return 加密后的十六进制字符串
     */
    public static String encrypt(String data, String password) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = generateKey(password);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encrypted);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * AES解密
     *
     * @param data     加密的十六进制字符串
     * @param password 解密密码
     * @return 解密后的原始数据
     */
    public static String decrypt(String data, String password) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = generateKey(password);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] encrypted = hexToBytes(data);
            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 字节数组转十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 十六进制字符串转字节数组
     */
    private static byte[] hexToBytes(String hex) {
        if (hex == null || hex.isEmpty()) {
            return new byte[0];
        }

        int length = hex.length();
        byte[] result = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            result[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
        }
        return result;
    }

    /**
     * 根据密码生成AES密钥
     */
    private static SecretKeySpec generateKey(String password) {
        try {
            // 使用SHA-256生成固定长度的密钥
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            // AES只支持128、192、256位，取前128位（16字节）
            byte[] key = Arrays.copyOf(hash, 16);
            return new SecretKeySpec(key, "AES");
        } catch (Exception e) {
            throw new RuntimeException("密钥生成失败", e);
        }
    }
}

