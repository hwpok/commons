package com.hwpok.commons.util.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 文件操作工具类
 *
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@SuppressWarnings("unused")
public final class FileUtils {

    private FileUtils() {
    }

    /**
     * 读取文本文件内容
     *
     * @param fileName 文件路径
     * @return 文件内容，失败返回空字符串
     */
    public static String readTextFile(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "";
        }

        File file = new File(fileName);
        if (!file.exists() || !file.isFile()) {
            return "";
        }

        try {
            return Files.readString(Paths.get(fileName));
        } catch (IOException e) {
            throw new RuntimeException("读取文件失败: " + fileName, e);
        }
    }

    /**
     * 写入文本文件
     *
     * @param fileName 文件路径
     * @param content 文件内容
     * @return 是否写入成功
     */
    public static boolean writeTextFile(String fileName, String content) {
        if (fileName == null || content == null) {
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, StandardCharsets.UTF_8))) {
            writer.write(content);
            return true;
        } catch (IOException e) {
            throw new RuntimeException("写入文件失败: " + fileName, e);
        }
    }

    /**
     * 生成唯一的文件名
     *
     * @param originalFileName 原始文件名
     * @return 带UUID的唯一文件名
     */
    public static String generateUniqueFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.trim().isEmpty()) {
            return UUID.randomUUID().toString().replace("-", "");
        }

        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid + "-" + originalFileName;
    }

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名
     * @return 扩展名（不包含点），无扩展名返回空字符串
     */
    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "";
        }

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }

        return fileName.substring(lastDotIndex + 1);
    }

    /**
     * 获取不带扩展名的文件名
     *
     * @param fileName 文件名
     * @return 不带扩展名的文件名
     */
    public static String getFileNameWithoutExtension(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "";
        }

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return fileName;
        }

        return fileName.substring(0, lastDotIndex);
    }

    /**
     * 检查文件是否存在
     *
     * @param fileName 文件路径
     * @return 文件是否存在
     */
    public static boolean exists(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return false;
        }

        return new File(fileName).exists();
    }

    /**
     * 创建目录（如果不存在）
     *
     * @param dirPath 目录路径
     * @return 是否创建成功或已存在
     */
    public static boolean createDirectoryIfNotExists(String dirPath) {
        if (dirPath == null || dirPath.trim().isEmpty()) {
            return false;
        }

        File dir = new File(dirPath);
        if (!dir.exists()) {
            return dir.mkdirs();
        }

        return dir.isDirectory();
    }
}

