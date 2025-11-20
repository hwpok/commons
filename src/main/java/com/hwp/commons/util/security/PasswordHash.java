package com.hwp.commons.util.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * PBKDF2密码哈希工具（简化版）
 * <p>
 * 使用Base64编码，固定迭代次数，API简洁
 *
 * @author wanpeng.hui
 * @since 2018-11-28
 */
@SuppressWarnings("unused")
public final class PasswordHash {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 8546;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;
    private static final String SEPARATOR = "$";

    private PasswordHash() {
    }

    /**
     * 生成随机盐值
     */
    public static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    /**
     * 生成随机盐值（Base64字符串）
     */
    public static String generateSaltString() {
        return Base64.getEncoder().encodeToString(generateSalt());
    }

    /**
     * 哈希密码（字节数组版本）
     */
    public static String hashPassword(byte[] password, byte[] salt) {
        if (password == null || salt == null) {
            return null;
        }
        return hashPasswordInternal(password, salt);
    }

    /**
     * 哈希密码（字符串版本）
     */
    public static String hashPassword(String password, String salt) {
        if (password == null || salt == null) {
            return null;
        }
        byte[] saltBytes = Base64.getDecoder().decode(salt);
        return hashPassword(password.getBytes(StandardCharsets.UTF_8), saltBytes);
    }

    /**
     * 内部哈希方法
     */
    private static String hashPasswordInternal(byte[] password, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(
                    new String(password, StandardCharsets.UTF_8).toCharArray(),
                    salt,
                    ITERATIONS,
                    KEY_LENGTH
            );
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            return null;
        }
    }

    /**
     * 创建完整的密码哈希（包含盐）
     * 格式：salt$hash
     */
    public static String encodePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return null;
        }

        byte[] salt = generateSalt();
        String hash = hashPasswordInternal(password.getBytes(StandardCharsets.UTF_8), salt);

        return Base64.getEncoder().encodeToString(salt) + SEPARATOR + hash;
    }

    /**
     * 验证密码（字节数组版本）
     */
    public static boolean matches(byte[] password, byte[] salt, String hashedPass) {
        if (hashedPass == null) return false;
        String recomputedHash = hashPassword(password, salt);
        return recomputedHash != null && recomputedHash.equals(hashedPass);
    }

    /**
     * 验证密码（字符串版本）
     */
    public static boolean matches(String password, String salt, String hash) {
        if (password == null || salt == null || hash == null) return false;
        return matches(password.getBytes(StandardCharsets.UTF_8),
                Base64.getDecoder().decode(salt),
                hash);
    }

    /**
     * 验证密码（完整哈希版本）
     */
    public static boolean matches(String password, String fullHash) {
        if (password == null || fullHash == null) return false;

        try {
            String[] parts = fullHash.split("\\" + SEPARATOR);
            if (parts.length != 2) return false;

            String salt = parts[0];
            String expectedHash = parts[1];
            String actualHash = hashPassword(password, salt);
            return actualHash != null && actualHash.equals(expectedHash);
        } catch (Exception e) {
            return false;
        }
    }
}
