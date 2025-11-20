package com.hwp.commons.util.security;

import java.util.Base64;

public class PasswordHashTestCase {
    public static void main(String[] args) {
        String password = "Ant198307";

        System.out.println("推荐方式：完整哈希=========================");
        String fullHash = PasswordHash.encodePassword(password);
        System.out.println("完整哈希: " + fullHash);
        System.out.println("验证: " + PasswordHash.matches(password, fullHash));

        System.out.println("分离方式=================================");
        String salt = PasswordHash.generateSaltString();
        String hash = PasswordHash.hashPassword(password, salt);
        System.out.println("分离哈希: " + hash);
        System.out.println("验证: " + PasswordHash.matches(password, salt, hash));

        System.out.println("对比编译长度==============================");
        byte[] testData = PasswordHash.generateSalt();
        String base64 = Base64.getEncoder().encodeToString(testData);
        System.out.println("原始长度: " + testData.length + " bytes");
        System.out.println("Base64长度: " + base64.length() + " chars");
    }
}
