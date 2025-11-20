package com.hwpok.commons.util.io;


import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 文件路径工具类
 * <p>
 * 提供获取应用运行路径、JAR包路径等常用功能。
 * 支持不同操作系统，处理特殊字符和空格。
 * </p>
 *
 * @author wanpeng.hui
 * @since 2021-01-01
 */
@SuppressWarnings("unused")
public final class FilePathUtils {

    private FilePathUtils() {
    }

    /**
     * 获取JAR文件所在目录
     *
     * @return JAR文件所在目录的绝对路径，不以文件分隔符结尾
     */
    public static String getJarDir() {
        try {
            URL url = FilePathUtils.class.getProtectionDomain().getCodeSource().getLocation();
            String path = decodeUrl(url.getPath());

            // 处理JAR文件路径
            if (path.endsWith(".jar")) {
                // 使用File.separator处理跨平台
                int lastSeparator = path.lastIndexOf(File.separator);
                if (lastSeparator > 0) {
                    path = path.substring(0, lastSeparator + 1);
                }
            }

            // 转换为绝对路径
            File file = new File(path);
            return file.getAbsoluteFile().getAbsolutePath();

        } catch (Exception e) {
            // 降级处理：返回当前工作目录
            return System.getProperty("user.dir");
        }
    }

    /**
     * 获取JAR文件的完整路径
     *
     * @return JAR文件路径，如果不是JAR运行则返回类的路径
     */
    public static String getJarPath() {
        try {
            URL url = FilePathUtils.class.getProtectionDomain().getCodeSource().getLocation();
            String path = decodeUrl(url.getPath());

            File file = new File(path);
            return file.getAbsoluteFile().getAbsolutePath();

        } catch (Exception e) {
            // 降级处理：返回当前类的路径
            return getCurrentClassPath();
        }
    }

    /**
     * 获取当前类的路径
     *
     * @return 类文件所在目录
     */
    public static String getCurrentClassPath() {
        try {
            String path = Objects.requireNonNull(FilePathUtils.class.getResource("")).getPath();
            path = decodeUrl(path);

            File file = new File(path);
            return file.getAbsoluteFile().getAbsolutePath();

        } catch (Exception e) {
            return System.getProperty("user.dir");
        }
    }

    /**
     * 获取用户主目录
     *
     * @return 用户主目录路径
     */
    public static String getUserHome() {
        return System.getProperty("user.home");
    }

    /**
     * 获取当前工作目录
     *
     * @return 当前工作目录路径
     */
    public static String getCurrentDir() {
        return System.getProperty("user.dir");
    }

    /**
     * 获取临时目录
     *
     * @return 系统临时目录路径
     */
    public static String getTempDir() {
        return System.getProperty("java.io.tmpdir");
    }

    /**
     * 规范化路径（统一使用系统分隔符）
     *
     * @param path 原始路径
     * @return 规范化后的路径
     */
    public static String normalizePath(String path) {
        if (path == null || path.isEmpty()) {
            return "";
        }

        // 替换所有分隔符为系统分隔符
        return path.replace("/", File.separator)
                .replace("\\", File.separator);
    }

    /**
     * 确保路径以分隔符结尾
     *
     * @param path 路径
     * @return 以分隔符结尾的路径
     */
    public static String ensureEndsWithSeparator(String path) {
        if (path == null || path.isEmpty()) {
            return File.separator;
        }

        if (!path.endsWith(File.separator)) {
            return path + File.separator;
        }
        return path;
    }

    /**
     * 安全解码URL
     *
     * @param url URL字符串
     * @return 解码后的字符串
     */
    private static String decodeUrl(String url) {
        if (url == null) {
            return "";
        }

        return URLDecoder.decode(url, StandardCharsets.UTF_8);
    }
}
