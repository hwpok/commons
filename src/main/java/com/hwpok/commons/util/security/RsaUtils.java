package com.hwpok.commons.util.security;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>RSA 非对称加密工具类</p>
 * <p>
 * <strong>核心功能：</strong>
 * <ul>
 *   <li>数字签名与验签</li>
 *   <li>小数据加密与解密（如对称密钥）</li>
 * </ul>
 * <strong>重要提示：</strong>RSA 不适合加密长文本，请使用混合加密（RSA+AES）。
 * 只支持对190字节的数据加密码, 汉字约63个, 字母符号约190个
 * </p>
 *
 * @author Wanpeng.Hui
 * @since 2020/12/12 14:08
 */
public final class RsaUtils {

    // --- 常量定义 ---
    private static final String ALGORITHM = "RSA";
    // 使用更安全的 OAEP 填充
    private static final String CIPHER_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    // 使用更安全的签名算法
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    // OAEP With SHA-256 的哈希长度
    private static final int OAEP_SHA256_HASH_LEN = 32;

    private RsaUtils() {
    }

    // ==============================
    // 密钥对生成
    // ==============================

    /**
     * 生成 RSA 密钥对
     *
     * @param keySize 密钥长度，推荐 2048 或 4096
     * @return 包含 Base64 编码公私钥的 Map
     */
    public static Map<String, String> generateKeyPair(int keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(keySize);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        Map<String, String> keyMap = new HashMap<>(2);
        keyMap.put("privateKey", Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
        keyMap.put("publicKey", Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
        return keyMap;
    }

    // ==============================
    // 核心：字节级加密解密 (供内部调用)
    // ==============================

    /**
     * 核心加密方法（字节级）
     */
    private static byte[] encryptBytes(byte[] data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        int maxBlockSize = getMaxEncryptBlock(publicKey);
        if (data.length > maxBlockSize) {
            throw new IllegalArgumentException("Data too long for RSA encryption. Max length is " + maxBlockSize + " bytes, but got " + data.length + " bytes.");
        }
        return cipher.doFinal(data);
    }

    /**
     * 核心解密方法（字节级）
     */
    private static byte[] decryptBytes(byte[] encryptedData, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encryptedData);
    }

    // ==============================
    // API: Base64 字符串加密解密 (处理二进制数据)
    // ==============================

    /**
     * 公钥加密 (输入和输出均为 Base64 字符串)
     *
     * @param dataBase64      Base64 编码的原始数据
     * @param publicKeyBase64 Base64 编码的公钥
     * @return Base64 编码的密文
     * @throws Exception 如果数据过长或加密失败
     */
    public static String encrypt(String dataBase64, String publicKeyBase64) throws Exception {
        byte[] data = Base64.getDecoder().decode(dataBase64);
        PublicKey publicKey = getPublicKey(publicKeyBase64);
        byte[] encryptedData = encryptBytes(data, publicKey);
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    /**
     * 私钥解密 (输入和输出均为 Base64 字符串)
     *
     * @param encryptedDataBase64 Base64 编码的密文
     * @param privateKeyBase64    Base64 编码的私钥
     * @return Base64 编码的原始数据
     * @throws Exception 如果解密失败
     */
    public static String decrypt(String encryptedDataBase64, String privateKeyBase64) throws Exception {
        byte[] encryptedData = Base64.getDecoder().decode(encryptedDataBase64);
        PrivateKey privateKey = getPrivateKey(privateKeyBase64);
        byte[] decryptedData = decryptBytes(encryptedData, privateKey);
        return Base64.getEncoder().encodeToString(decryptedData);
    }

    // ==============================
    // API: 普通字符串加密解密 (新增)
    // ==============================

    /**
     * 公钥加密普通字符串
     *
     * @param plainText       原始明文字符串
     * @param publicKeyBase64 Base64 编码的公钥
     * @return Base64 编码的密文
     * @throws Exception 如果数据过长或加密失败
     */
    public static String encryptString(String plainText, String publicKeyBase64) throws Exception {
        if (plainText == null) {
            return null;
        }
        byte[] data = plainText.getBytes(StandardCharsets.UTF_8);
        PublicKey publicKey = getPublicKey(publicKeyBase64);
        byte[] encryptedData = encryptBytes(data, publicKey);
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    /**
     * 私钥解密为普通字符串
     *
     * @param encryptedText    Base64 编码的密文
     * @param privateKeyBase64 Base64 编码的私钥
     * @return 解密后的明文字符串
     * @throws Exception 如果解密失败
     */
    public static String decryptString(String encryptedText, String privateKeyBase64) throws Exception {
        if (encryptedText == null) {
            return null;
        }
        byte[] encryptedData = Base64.getDecoder().decode(encryptedText);
        PrivateKey privateKey = getPrivateKey(privateKeyBase64);
        byte[] decryptedData = decryptBytes(encryptedData, privateKey);
        return new String(decryptedData, StandardCharsets.UTF_8);
    }

    // ==============================
    // API: 签名与验签
    // ==============================

    /**
     * 用私钥对信息生成数字签名
     *
     * @param dataBase64       待签名的数据
     * @param privateKeyBase64 Base64 编码的私钥
     * @return Base64 编码的签名字符串
     */
    public static String sign(String dataBase64, String privateKeyBase64) throws Exception {
        byte[] data = Base64.getDecoder().decode(dataBase64);
        PrivateKey privateKey = getPrivateKey(privateKeyBase64);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data);
        return Base64.getEncoder().encodeToString(signature.sign());
    }

    /**
     * 用私钥对普通字符串生成数字签名
     */
    public static String signString(String plainText, String privateKeyBase64) throws Exception {
        if (plainText == null) {
            return null;
        }
        byte[] data = plainText.getBytes(StandardCharsets.UTF_8);
        PrivateKey privateKey = getPrivateKey(privateKeyBase64);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data);
        return Base64.getEncoder().encodeToString(signature.sign());
    }

    /**
     * 校验数字签名
     *
     * @param dataBase64      原始数据
     * @param signBase64      Base64 编码的签名
     * @param publicKeyBase64 Base64 编码的公钥
     * @return 是否校验通过
     */
    public static boolean verify(String dataBase64, String signBase64, String publicKeyBase64) throws Exception {
        byte[] data = Base64.getDecoder().decode(dataBase64);
        PublicKey publicKey = getPublicKey(publicKeyBase64);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(Base64.getDecoder().decode(signBase64));
    }

    /**
     * 校验普通字符串的数字签名
     */
    public static boolean verifyString(String plainText, String signBase64, String publicKeyBase64) throws Exception {
        if (plainText == null || signBase64 == null) {
            return false;
        }
        byte[] data = plainText.getBytes(StandardCharsets.UTF_8);
        PublicKey publicKey = getPublicKey(publicKeyBase64);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(Base64.getDecoder().decode(signBase64));
    }

    // ==============================
    // 私有辅助方法
    // ==============================

    private static PrivateKey getPrivateKey(String keyBase64) throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }

    private static PublicKey getPublicKey(String keyBase64) throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 根据公钥动态计算最大加密块大小
     * 公式: k - 2*hLen - 2
     * 其中 k 是密钥长度(字节), hLen 是哈希函数输出长度(字节)
     * 对于 SHA-256, hLen = 32, 所以开销是 66 字节
     */
    private static int getMaxEncryptBlock(PublicKey publicKey) {
        int keySizeBytes = ((java.security.interfaces.RSAPublicKey) publicKey).getModulus().bitLength() / 8;
        return keySizeBytes - (2 * OAEP_SHA256_HASH_LEN) - 2; // 即 keySizeBytes - 66
    }
}
