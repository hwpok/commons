package com.hwpok.commons.util.convert;


import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * URI/URL 工具类（基于 java.net.URI 实现，符合 RFC 3986 标准）
 *
 * @author wanpeng.hui
 */
@SuppressWarnings("unused")
public final class UriUtils {

    private UriUtils() {
    }

    /**
     * 安全解析 URI，处理 null 和非法格式
     */
    private static URI parseUri(String url) {
        if (url == null) {
            return null;
        }
        try {
            // 兼容不带协议的相对路径（如 "/api/test"）
            if (!url.contains("://")) {
                url = "http://dummy" + (url.startsWith("/") ? url : "/" + url);
            }
            return URI.create(url);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取协议（scheme），如 http, https, ftp
     */
    public static String getScheme(String url) {
        URI uri = parseUri(url);
        return uri != null ? uri.getScheme() : null;
    }

    /**
     * 获取主机名（host），自动去除端口和用户信息
     */
    public static String getHost(String url) {
        URI uri = parseUri(url);
        return uri != null ? uri.getHost() : null;
    }

    /**
     * 获取端口号
     */
    public static int getPort(String url) {
        URI uri = parseUri(url);
        return uri != null ? uri.getPort() : -1;
    }

    /**
     * 获取路径部分（不含查询参数和 fragment），如 /api/user
     */
    public static String getPath(String url) {
        URI uri = parseUri(url);
        return uri != null ? uri.getPath() : null;
    }

    /**
     * 获取查询字符串（不含 '?'，且在 '#' 之前）
     */
    public static String getQuery(String url) {
        URI uri = parseUri(url);
        return uri != null ? uri.getQuery() : null;
    }

    /**
     * 移除查询参数和 fragment，返回基础 URL
     */
    public static String removeQueryAndFragment(String url) {
        if (url == null) return null;
        int queryIndex = url.indexOf('?');
        int fragIndex = url.indexOf('#');
        int endIndex = Math.min(
                queryIndex >= 0 ? queryIndex : Integer.MAX_VALUE,
                fragIndex >= 0 ? fragIndex : Integer.MAX_VALUE
        );
        return endIndex == Integer.MAX_VALUE ? url : url.substring(0, endIndex);
    }

    /**
     * 解析查询参数为 Map（已 URL 解码，UTF-8）
     * <p>
     * 注意：重复 key 会覆盖（保留最后一个）
     */
    public static Map<String, String> getQueryParams(String url) {
        Map<String, String> params = new HashMap<>();
        String query = getQuery(url);
        if (query == null || query.isEmpty()) {
            return params;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            if (pair.isEmpty()) continue;
            int idx = pair.indexOf('=');
            String key = idx > 0 ? decode(pair.substring(0, idx)) : decode(pair);
            String value = idx > 0 && idx < pair.length() - 1 ? decode(pair.substring(idx + 1)) : "";
            if (key != null) {
                params.put(key, value);
            }
        }
        return params;
    }

    /**
     * URL 解码（UTF-8）
     */
    private static String decode(String s) {
        if (s == null || s.isEmpty()) return s;
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }
}
