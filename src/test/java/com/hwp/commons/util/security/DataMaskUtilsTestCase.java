package com.hwp.commons.util.security;

public class DataMaskUtilsTestCase {

    public static void main(String[] args) {
        String phone = "13812345678";
        String email = "zhangsan@example.com";
        String idCard = "110101199001011234";

        // 完全脱敏
        System.out.println("完全脱敏:");
        System.out.println("手机号: " + DataMaskUtils.mask(phone));
        System.out.println("邮箱: " + DataMaskUtils.mask(email));
        System.out.println("身份证: " + DataMaskUtils.mask(idCard));

        // 部分脱敏
        System.out.println("\n部分脱敏:");
        System.out.println("手机号: " + DataMaskUtils.maskPartial(phone, 3, 4));
        System.out.println("邮箱: " + DataMaskUtils.maskPartial(email, 3, 10));
        System.out.println("身份证: " + DataMaskUtils.maskPartial(idCard, 6, 4));

        // 自定义占位符
        System.out.println("\n自定义占位符:");
        System.out.println("手机号: " + DataMaskUtils.maskPartial(phone, 3, 4, "*"));
    }
}
