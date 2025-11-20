package com.hwp.commons.util.security;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class ECCUtilsTestCase {
    // 测试方法（生产环境可删除）
    public static void main(String[] args) throws Exception {
        // 生成密钥对
        KeyPair keyPair = ECCUtils.generateKeyPair(256);
        String privateKey = ECCUtils.encodePrivateKey(keyPair);
        String publicKey = ECCUtils.encodePublicKey(keyPair);
        System.out.println("私钥: " + privateKey);
        System.out.println("公钥: " + publicKey);

        String message = "Hello ECC Signature";

        // 签名
        String signature = ECCUtils.signData(privateKey, message);
        System.out.println("签名: " + signature);

        // 验证签名
        boolean isValid = ECCUtils.verifySignature(publicKey, signature, message);
        System.out.println("验证结果: " + isValid);

        // 测试解码方法
        PrivateKey decodedPrivateKey = ECCUtils.decodePrivateKey(privateKey);
        PublicKey decodedPublicKey = ECCUtils.decodePublicKey(publicKey);
        System.out.println("解码成功: " + (decodedPrivateKey != null && decodedPublicKey != null));
    }
}
