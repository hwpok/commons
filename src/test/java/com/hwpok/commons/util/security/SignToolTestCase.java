package com.hwpok.commons.util.security;

import java.util.HashMap;
import java.util.Map;

public class SignToolTestCase {
    public static void main(String[] args) {
        Map<String, String> params = new HashMap<>();
        params.put("a", "adfa");
        params.put("z", "adfa");
        params.put("4", "adfa");
        params.put("ab", "adfa");
        params.put("aa", "adfa");
        params.put("az", "adfa");
        params.put("p", "adfa");
        params.put("0", "adfa");

        // 新的多算法方式
        String sha1Sig = SignTool.sign(params, "aa", SignTool.Algorithm.HMAC_SHA1);
        String sha512Sig = SignTool.sign(params, "aa", SignTool.Algorithm.HMAC_SHA512);
        System.out.printf("Len: %d, SHA1: %s\n", sha1Sig.length(), sha1Sig);
        System.out.printf("Len: %d, SHA512: %s\n", sha512Sig.length(), sha512Sig);

        // 验证
        boolean verified = SignTool.verify(sha1Sig, params, "aa", SignTool.Algorithm.HMAC_SHA1);
        System.out.println("SHA1 Verified: " + verified);
        boolean verified512 = SignTool.verify(sha512Sig, params, "aa", SignTool.Algorithm.HMAC_SHA512);
        System.out.println("SHA512 Verified: " + verified512);
    }
}
