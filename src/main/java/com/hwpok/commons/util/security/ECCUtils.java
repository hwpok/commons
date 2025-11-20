package com.hwpok.commons.util.security;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * ECC椭圆曲线加密工具类
 *
 * @author wanpeng.hui
 * @since 2018-11-28
 */
public final class ECCUtils {

    private static final String ALGORITHM_EC = "EC";
    private static final String SIGNATURE_ALGORITHM = "SHA256withECDSA";

    private ECCUtils() {
    }

    /**
     * 生成ECC密钥对
     *
     * @param keySize 密钥长度(192|224|239|256|384|521)
     * @return 密钥对
     * @throws NoSuchAlgorithmException 算法不支持异常
     */
    public static KeyPair generateKeyPair(int keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM_EC);
        keyPairGenerator.initialize(keySize, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 获取公钥Base64编码
     *
     * @param keyPair 密钥对
     * @return Base64编码的公钥
     */
    public static String encodePublicKey(KeyPair keyPair) {
        if (keyPair == null) {
            throw new IllegalArgumentException("KeyPair cannot be null");
        }
        ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    /**
     * 获取私钥Base64编码
     *
     * @param keyPair 密钥对
     * @return Base64编码的私钥
     */
    public static String encodePrivateKey(KeyPair keyPair) {
        if (keyPair == null) {
            throw new IllegalArgumentException("KeyPair cannot be null");
        }
        ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    /**
     * 数据签名
     *
     * @param privateKeyBase64 Base64编码的私钥
     * @param data             待签名数据
     * @return Base64编码的签名
     * @throws Exception 签名异常
     */
    public static String signData(String privateKeyBase64, String data) throws Exception {
        if (privateKeyBase64 == null || data == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }

        PrivateKey privateKey = decodePrivateKey(privateKeyBase64);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data.getBytes(StandardCharsets.UTF_8));
        byte[] signBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signBytes);
    }

    /**
     * 验证签名
     *
     * @param publicKeyBase64 Base64编码的公钥
     * @param signatureBase64 Base64编码的签名
     * @param data            原始数据
     * @return 验证结果
     * @throws Exception 验证异常
     */
    public static boolean verifySignature(String publicKeyBase64, String signatureBase64, String data) throws Exception {
        if (publicKeyBase64 == null || signatureBase64 == null || data == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }

        PublicKey publicKey = decodePublicKey(publicKeyBase64);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(data.getBytes(StandardCharsets.UTF_8));
        return signature.verify(Base64.getDecoder().decode(signatureBase64));
    }

    /**
     * 从Base64字符串解码私钥
     *
     * @param privateKeyBase64 Base64编码的私钥
     * @return 私钥对象
     * @throws Exception 解码异常
     */
    public static PrivateKey decodePrivateKey(String privateKeyBase64) throws Exception {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyBase64));
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_EC);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 从Base64字符串解码公钥
     *
     * @param publicKeyBase64 Base64编码的公钥
     * @return 公钥对象
     * @throws Exception 解码异常
     */
    public static PublicKey decodePublicKey(String publicKeyBase64) throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyBase64));
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_EC);
        return keyFactory.generatePublic(keySpec);
    }
}

