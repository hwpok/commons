package com.hwpok.commons.util.system;

/**
 * 安全的 SQL 模糊查询工具类（自动转义通配符）
 *
 * @author wanpeng.hui
 */
@SuppressWarnings("unused")
public final class SqlUtils {

    private SqlUtils() {
    }

    /**
     * 转义 SQL LIKE 通配符: '%' -> '\%', '_' -> '\_', '\' -> '\\'
     *
     * @param input 原始关键字
     * @return 转义后的字符串
     */
    public static String escapeLike(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    /**
     * 左模糊右精确（安全）
     * 使用时需配合 ESCAPE '\\'（例如 MyBatis 中）
     */
    public static String likeLeft(String keyword) {
        return "%" + escapeLike(keyword);
    }

    /**
     * 左精确右模糊（安全）
     */
    public static String likeRight(String keyword) {
        return escapeLike(keyword) + "%";
    }

    /**
     * 全模糊（安全）
     */
    public static String like(String keyword) {
        return "%" + escapeLike(keyword) + "%";
    }
}