package com.hwpok.commons.util.identity;

import org.junit.jupiter.api.Test;

public class MachineIdTestCase {
    @Test
    void testMachineId() {
        String getSecurityCode = MachineId.getCpuId();
        System.out.println(getSecurityCode);

    }
}
