package com.hwpok.commons.util.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * GZIP 压缩/解压工具类（基于字节数组）。
 * <p>
 * 注意：压缩结果为二进制数据，若需在文本环境（如 HTTP、JSON）中传输，
 * 请配合 Base64 编码使用（如 {@code Base64.getEncoder().encodeToString(compress(...))}）。
 *
 * @author wanpeng.hui
 */
@SuppressWarnings("unused")
public final class ZipUtils {

    private ZipUtils() {
    }

    /**
     * 使用 GZIP 压缩字符串（UTF-8 编码）。
     *
     * @param str 待压缩的字符串（null 返回 null）
     * @return 压缩后的字节数组，若输入为 null 则返回 null
     * @throws IOException 压缩过程中发生 IO 异常
     */
    public static byte[] compress(String str) throws IOException {
        if (str == null) {
            return null;
        }
        byte[] input = str.getBytes(StandardCharsets.UTF_8);
        return compress(input);
    }

    /**
     * 使用 GZIP 压缩字节数组。
     *
     * @param data 待压缩的数据（null 返回 null）
     * @return 压缩后的字节数组
     * @throws IOException 压缩过程中发生 IO 异常
     */
    public static byte[] compress(byte[] data) throws IOException {
        if (data == null || data.length == 0) {
            return data;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GZIPOutputStream gzos = new GZIPOutputStream(baos)) {
            gzos.write(data);
        }
        return baos.toByteArray();
    }

    /**
     * 解压 GZIP 数据并还原为字符串（UTF-8 解码）。
     *
     * @param compressed 压缩后的字节数组（null 返回 null）
     * @return 解压后的字符串
     * @throws IOException 解压过程中发生 IO 异常
     */
    public static String uncompress(byte[] compressed) throws IOException {
        if (compressed == null || compressed.length == 0) {
            return null;
        }
        byte[] decompressed = uncompressToBytes(compressed);
        return new String(decompressed, StandardCharsets.UTF_8);
    }

    /**
     * 解压 GZIP 字节数组。
     *
     * @param compressed 压缩数据
     * @return 原始字节数组
     */
    public static byte[] uncompressToBytes(byte[] compressed) throws IOException {
        if (compressed == null || compressed.length == 0) {
            return compressed;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ByteArrayInputStream bais = new ByteArrayInputStream(compressed);
             GZIPInputStream gzis = new GZIPInputStream(bais)) {
            byte[] buffer = new byte[1024];
            int n;
            while ((n = gzis.read(buffer)) >= 0) {
                baos.write(buffer, 0, n);
            }
        }
        return baos.toByteArray();
    }
}
