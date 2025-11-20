package com.hwp.commons.util.convert;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Base62 编码工具类（支持 GMP 和 Inverted 字符集）
 * <p>
 * 线程安全、不可变。
 *
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@SuppressWarnings("unused")
public final class Base62 {

    private static final BigInteger BASE_256 = BigInteger.valueOf(256);
    private static final BigInteger BASE_62 = BigInteger.valueOf(62);

    private final byte[] alphabet;
    private final boolean[] validChar; // 用于快速校验字符是否合法

    // GMP 字符集：0-9A-Za-z
    public static final byte[] GMP = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z'
    };

    // Inverted 字符集：0-9a-zA-Z
    public static final byte[] INVERTED = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    private Base62(byte[] alphabet) {
        this.alphabet = Arrays.copyOf(alphabet, alphabet.length);
        this.validChar = buildValidCharSet(this.alphabet);
    }

    /**
     * 创建使用 GMP 字符集的实例（推荐）
     */
    public static Base62 createInstance() {
        return createInstanceWithGmpCharacterSet();
    }

    public static Base62 createInstanceWithGmpCharacterSet() {
        return new Base62(GMP);
    }

    public static Base62 createInstanceWithInvertedCharacterSet() {
        return new Base62(INVERTED);
    }

    // 构建快速校验表：validChar[c] == true 表示 c 是合法字符
    private boolean[] buildValidCharSet(byte[] alphabet) {
        boolean[] set = new boolean[256];
        for (byte b : alphabet) {
            set[b & 0xFF] = true;
        }
        return set;
    }

    /**
     * 将字节数组编码为 Base62 字节数组
     */
    public byte[] encode(final byte[] message) {
        if (message == null || message.length == 0) {
            return new byte[0];
        }

        // 统计前导零数量
        int leadingZeros = 0;
        while (leadingZeros < message.length && message[leadingZeros] == 0) {
            leadingZeros++;
        }

        // 转为正数 BigInteger（signum = 1）
        BigInteger value = new BigInteger(1, message);

        if (value.equals(BigInteger.ZERO)) {
            // 全零情况
            byte[] result = new byte[leadingZeros + 1];
            Arrays.fill(result, alphabet[0]);
            return result;
        }

        // 执行 Base62 编码（低位在前）
        java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
        while (!value.equals(BigInteger.ZERO)) {
            BigInteger[] divRem = value.divideAndRemainder(BASE_62);
            buffer.write(alphabet[divRem[1].intValue()]);
            value = divRem[0];
        }

        // 反转得到高位在前
        byte[] encoded = reverse(buffer.toByteArray());

        // 在前面补上对应数量的 '0'（即 alphabet[0]）
        if (leadingZeros > 0) {
            byte[] withLeadingZeros = new byte[encoded.length + leadingZeros];
            Arrays.fill(withLeadingZeros, 0, leadingZeros, alphabet[0]);
            System.arraycopy(encoded, 0, withLeadingZeros, leadingZeros, encoded.length);
            return withLeadingZeros;
        }

        return encoded;
    }

    /**
     * 将字符串（UTF-8）编码为 Base62 字符串（ASCII）
     */
    public String encode(String message) {
        if (message == null) {
            return "";
        }
        byte[] encodedBytes = encode(message.getBytes(StandardCharsets.UTF_8));
        return new String(encodedBytes, StandardCharsets.US_ASCII);
    }

    /**
     * 将 Base62 字节数组解码为原始字节数组
     */
    public byte[] decode(final byte[] encoded) {
        if (encoded == null || encoded.length == 0) {
            return new byte[0];
        }

        // 校验所有字符是否合法
        for (int i = 0; i < encoded.length; i++) {
            byte b = encoded[i];
            if (!validChar[b & 0xFF]) {
                throw new IllegalArgumentException(
                        "Invalid Base62 character: '" + (char) b + "' at index " + i);
            }
        }

        // 统计前导 '0'（即 alphabet[0]）数量
        int leadingZeros = 0;
        while (leadingZeros < encoded.length && encoded[leadingZeros] == alphabet[0]) {
            leadingZeros++;
        }

        // 转为 BigInteger
        BigInteger value = BigInteger.ZERO;
        for (int i = leadingZeros; i < encoded.length; i++) {
            byte b = encoded[i];
            int index = -1;
            // 查找字符在 alphabet 中的位置
            for (int j = 0; j < alphabet.length; j++) {
                if (alphabet[j] == b) {
                    index = j;
                    break;
                }
            }
            if (index == -1) {
                // 理论不会发生（因已校验）
                throw new IllegalStateException("Character not found in alphabet: " + (char) b);
            }
            value = value.multiply(BASE_62).add(BigInteger.valueOf(index));
        }

        // 转回字节数组
        byte[] decoded = value.toByteArray();

        // BigInteger.toByteArray() 可能带符号位（首位为 0），需去除多余前导零
        int firstNonZero = 0;
        while (firstNonZero < decoded.length && decoded[firstNonZero] == 0) {
            firstNonZero++;
        }
        byte[] trimmed = Arrays.copyOfRange(decoded, firstNonZero, decoded.length);

        // 补上前导零（对应原始字节中的前导零）
        if (leadingZeros > 0) {
            byte[] result = new byte[trimmed.length + leadingZeros];
            Arrays.fill(result, 0, leadingZeros, (byte) 0);
            System.arraycopy(trimmed, 0, result, leadingZeros, trimmed.length);
            return result;
        }

        return trimmed.length == 0 ? new byte[]{0} : trimmed;
    }

    /**
     * 将 Base62 字符串（ASCII）解码为 UTF-8 字符串
     */
    public String decode(final String encoded) {
        if (encoded == null) {
            return "";
        }
        byte[] decodedBytes = decode(encoded.getBytes(StandardCharsets.US_ASCII));
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    // 工具方法：反转字节数组
    private static byte[] reverse(byte[] arr) {
        byte[] reversed = new byte[arr.length];
        for (int i = 0; i < arr.length; i++) {
            reversed[i] = arr[arr.length - 1 - i];
        }
        return reversed;
    }
}