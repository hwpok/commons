package com.hwpok.commons.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EnumResolverTestCase {
    @Test
    public void test() {
        Gender gender = EnumResolver.getByCode(Gender.class, (byte) 1);
        assertNotNull(gender);
        assertEquals("女",EnumResolver.getNameByCode(Gender.class, (byte) 0));
    }
}

