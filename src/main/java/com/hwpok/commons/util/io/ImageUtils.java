package com.hwpok.commons.util.io;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

/**
 * 图片工具类
 * <p>
 * 提供图片的加载、编码、格式转换等功能。
 * 支持从URL加载图片，转换为Base64编码等常用操作。
 * </p>
 *
 * @author wanpeng.hui
 * @since 2021-01-01
 */
public final class ImageUtils {

    /**
     * 默认图片格式
     */
    private static final String DEFAULT_FORMAT = "png";

    /**
     * 默认Base64前缀
     */
    private static final String DATA_URL_PREFIX = "data:image/%s;base64,";

    private ImageUtils() {
    }

    /**
     * 从URL加载图片
     *
     * @param imageUrl 图片URL
     * @return BufferedImage对象，加载失败返回null
     */
    public static BufferedImage load(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return null;
        }

        try {
            URL url = createUrl(imageUrl);
            return ImageIO.read(url);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 从URL加载图片（带超时）
     *
     * @param imageUrl 图片URL
     * @param timeout  超时时间（毫秒）
     * @return BufferedImage对象，加载失败返回null
     */
    public static BufferedImage load(String imageUrl, int timeout) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return null;
        }

        try {
            URL url = createUrl(imageUrl);
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
            return ImageIO.read(conn.getInputStream());
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 将图片URL转换为Base64编码
     *
     * @param imageUrl 图片URL
     * @return Base64编码字符串，失败返回null
     */
    public static String toBase64(String imageUrl) {
        return toBase64(imageUrl, DEFAULT_FORMAT, false);
    }

    /**
     * 将图片URL转换为Base64编码（带Data URL前缀）
     *
     * @param imageUrl   图片URL
     * @param format     图片格式（png、jpg等）
     * @param withPrefix 是否包含Data URL前缀
     * @return Base64编码字符串，失败返回null
     */
    public static String toBase64(String imageUrl, String format, boolean withPrefix) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return null;
        }

        try {
            URL url = createUrl(imageUrl);
            try (InputStream in = url.openStream();
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }

                String base64 = Base64.getEncoder().encodeToString(out.toByteArray());
                return withPrefix ? String.format(DATA_URL_PREFIX, format) + base64 : base64;
            }
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 将BufferedImage转换为Base64编码
     *
     * @param image 图片对象
     * @return Base64编码字符串，失败返回null
     */
    public static String toBase64(BufferedImage image) {
        return toBase64(image, DEFAULT_FORMAT, false);
    }

    /**
     * 将BufferedImage转换为Base64编码
     *
     * @param image      图片对象
     * @param format     图片格式（png、jpg等）
     * @param withPrefix 是否包含Data URL前缀
     * @return Base64编码字符串，失败返回null
     */
    public static String toBase64(BufferedImage image, String format, boolean withPrefix) {
        if (image == null) {
            return null;
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            boolean success = ImageIO.write(image, format, out);
            if (!success) {
                return null;
            }

            String base64 = Base64.getEncoder().encodeToString(out.toByteArray());
            return withPrefix ? String.format(DATA_URL_PREFIX, format) + base64 : base64;

        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 将BufferedImage转换为字节数组
     *
     * @param image 图片对象
     * @return 字节数组，失败返回null
     */
    public static byte[] toBytes(BufferedImage image) {
        return toBytes(image, DEFAULT_FORMAT);
    }

    /**
     * 将BufferedImage转换为指定格式的字节数组
     *
     * @param image  图片对象
     * @param format 图片格式
     * @return 字节数组，失败返回null
     */
    public static byte[] toBytes(BufferedImage image, String format) {
        if (image == null) {
            return null;
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            if (ImageIO.write(image, format, out)) {
                return out.toByteArray();
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 从字节数组创建BufferedImage
     *
     * @param imageBytes 图片字节数组
     * @return BufferedImage对象，失败返回null
     */
    public static BufferedImage fromBytes(byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length == 0) {
            return null;
        }

        try {
            return ImageIO.read(new java.io.ByteArrayInputStream(imageBytes));
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 安全创建URL对象
     *
     * @param urlString URL字符串
     * @return URL对象
     * @throws MalformedURLException URL格式错误
     */
    private static URL createUrl(String urlString) throws MalformedURLException {
        return new URL(urlString);
    }

    /**
     * 检查图片格式是否支持
     *
     * @param format 图片格式
     * @return true-支持，false-不支持
     */
    public static boolean isFormatSupported(String format) {
        if (format == null || format.trim().isEmpty()) {
            return false;
        }

        String[] readerFormats = ImageIO.getReaderFormatNames();
        for (String supportedFormat : readerFormats) {
            if (supportedFormat.equalsIgnoreCase(format)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取支持的图片格式列表
     *
     * @return 支持的格式数组
     */
    public static String[] getSupportedFormats() {
        return ImageIO.getReaderFormatNames();
    }
}
