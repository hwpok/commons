package com.hwpok.commons.util.security;

import java.util.Map;

public class RsaUtilsTestCase {
    public static void main(String[] args) throws Exception {
        Map<String, String> keyPair = RsaUtils.generateKeyPair(2048);
        String publicKey = keyPair.get("publicKey");
        String privateKey = keyPair.get("privateKey");

        System.out.println("--- 测试新增的字符串方法 ---");
        String message = "这是一个需要加密的敏感信息，比如用户的支付密码。";
        System.out.println("原始消息: " + message);

        // 字符串加密
        String encryptedMsg = RsaUtils.encryptString(message, publicKey);
        System.out.println("加密后: " + encryptedMsg);

        // 字符串解密
        String decryptedMsg = RsaUtils.decryptString(encryptedMsg, privateKey);
        System.out.println("解密后: " + decryptedMsg);
        System.out.println("解密成功: " + message.equals(decryptedMsg));

        System.out.println("\n--- 测试新增的字符串签名方法 ---");
        // 字符串签名
        String signature = RsaUtils.signString(message, privateKey);
        System.out.println("签名: " + signature);

        // 字符串验签
        boolean isVerified = RsaUtils.verifyString(message, signature, publicKey);
        System.out.println("验签结果: " + isVerified);

        System.out.println("\n--- 测试最大加密块大小限制 ---");
        // 生成一个长度为 190 的字符串
        String maxData = "a".repeat(190);
        System.out.println("尝试加密 190 字节的数据...");
        String encryptedMaxData = RsaUtils.encryptString(maxData, publicKey);
        System.out.println("加密成功!");

        // 生成一个长度为 191 的字符串
        String tooLongData = "b".repeat(191);
        System.out.println("尝试加密 191 字节的数据...");
        try {
            RsaUtils.encryptString(tooLongData, publicKey);
        } catch (IllegalArgumentException e) {
            System.out.println("成功捕获预期的异常: " + e.getMessage());
        }
    }
}
