package com.hwpok.commons.util.convert;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Base62TestCase {
    @Test
    void testEncodeDecodeString() {
        Base62 b62 = Base62.createInstance();
        String original = "Hello Base62!";
        String encoded = b62.encode(original);
        String decoded = b62.decode(encoded);
        assertEquals(original, decoded);
    }

    @Test
    void testLeadingZeros() {
        Base62 b62 = Base62.createInstance();
        byte[] input = new byte[]{0, 0, 127, -1};
        byte[] encoded = b62.encode(input);
        byte[] decoded = b62.decode(encoded);
        assertArrayEquals(input, decoded);
    }

    @Test
    void testEmptyAndNull() {
        Base62 b62 = Base62.createInstance();
        assertEquals("", b62.encode((String) null));
        assertEquals("", b62.decode((String) null));
        assertArrayEquals(new byte[0], b62.encode((byte[]) null));
        assertArrayEquals(new byte[0], b62.decode((byte[]) null));
    }
}
