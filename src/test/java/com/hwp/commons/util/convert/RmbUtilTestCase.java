package com.hwp.commons.util.convert;

import java.math.BigDecimal;

public class RmbUtilTestCase {
    public static void main(String[] args) {
        test(0);
        test(-0.01);
        test(123.45);
        test(10000);
        test(10001);
        test(10101.10);
        test(100000000);
        test(100000001);
        test(100100100.10);
        test(1234567890.12);
    }

    private static void test(double amount) {
        BigDecimal bd = BigDecimal.valueOf(amount);
        String result = RmbUtils.toCapital(bd);
        System.out.printf("金额: %15.2f  =>  %s%n", amount, result);
    }
}
