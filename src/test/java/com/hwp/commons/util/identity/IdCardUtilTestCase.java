package com.hwp.commons.util.identity;

import org.junit.jupiter.api.Test;

public class IdCardUtilTestCase {
    @Test
    public void test() {
        IdCardUtils.IdCardInfo info = IdCardUtils.parse("511321198305100914");
        System.out.println(info);
    }
}
