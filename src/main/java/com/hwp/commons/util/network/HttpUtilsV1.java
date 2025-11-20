package com.hwp.commons.util.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * HTTP 请求工具类（支持 GET/POST，兼容 HTTP/HTTPS）。
 * <p>
 * 注意：HTTPS 使用系统默认信任证书，不跳过主机名校验（安全）。
 * 如需自定义 SSL，请使用 Apache HttpClient 或 OkHttp。
 *
 * @author wanpeng.hui
 * @since 2018-11-28
 */
@SuppressWarnings("unused")
public final class HttpUtilsV1 {

    /**
     * 连接超时时间：30秒
     */
    private static final int CONNECT_TIMEOUT = 30_000;

    /**
     * 读取超时时间：10秒
     */
    private static final int READ_TIMEOUT = 10_000;

    /**
     * 表单提交的内容类型（含 UTF-8 编码声明）
     */
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded; charset=UTF-8";

    /**
     * JSON 提交的内容类型（含 UTF-8 编码声明）
     */
    public static final String CONTENT_TYPE_JSON = "application/json; charset=UTF-8";

    // 私有构造函数，防止实例化
    private HttpUtilsV1() {
    }

    /**
     * 发送 GET 请求。
     *
     * @param url    请求地址
     * @param params 请求参数（key-value 形式，可为 null）
     * @return 响应体字符串（UTF-8 解码），若请求失败则抛出异常
     */
    public static String doGet(String url, Map<String, String> params) {
        String queryString = buildQueryString(params);
        String fullUrl = url + (queryString.isEmpty() ? "" : "?" + queryString);
        return sendRequest(fullUrl, "GET", null, null);
    }

    /**
     * 发送 POST 表单请求（application/x-www-form-urlencoded）。
     *
     * @param url    请求地址
     * @param params 表单参数（key-value 形式，可为 null）
     * @return 响应体字符串（UTF-8 解码），若请求失败则抛出异常
     */
    public static String doPostForm(String url, Map<String, String> params) {
        String body = buildFormBody(params);
        return sendRequest(url, "POST", body, CONTENT_TYPE_FORM);
    }

    /**
     * 发送 POST JSON 请求（application/json）。
     *
     * @param url      请求地址
     * @param jsonBody JSON 字符串（如：{"name":"张三"}）
     * @return 响应体字符串（UTF-8 解码），若请求失败则抛出异常
     */
    public static String doPostJson(String url, String jsonBody) {
        return sendRequest(url, "POST", jsonBody, CONTENT_TYPE_JSON);
    }

    /**
     * 核心请求发送方法。
     *
     * @param urlStr      完整 URL（含查询参数）
     * @param method      HTTP 方法（如 "GET", "POST"）
     * @param body        请求体内容（POST 时使用，GET 可为 null）
     * @param contentType 内容类型（POST 时设置，GET 可为 null）
     * @return 响应内容（无论成功或失败状态码，均返回响应体）
     * @throws RuntimeException 网络异常或 IO 错误时抛出
     */
    private static String sendRequest(String urlStr, String method, String body, String contentType) {
        HttpURLConnection conn = null;
        try {
            // 使用 URI.create 避免 URL 的 DNS 副作用，再转为 URL
            URL url = URI.create(urlStr).toURL();
            conn = (HttpURLConnection) url.openConnection();

            // 设置基础参数
            conn.setRequestMethod(method);
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setUseCaches(false);

            // POST 请求需写入请求体
            if ("POST".equals(method)) {
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", contentType);
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = body.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }

            // 判断是否为成功响应（2xx）
            int responseCode = conn.getResponseCode();
            boolean isSuccess = responseCode >= 200 && responseCode < 300;

            // 读取响应流（成功读 inputStream，失败读 errorStream）
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    isSuccess ? conn.getInputStream() : conn.getErrorStream(),
                    StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line).append(System.lineSeparator());
                }
                return response.toString().trim(); // 去除首尾空白
            }

        } catch (Exception e) {
            throw new RuntimeException("HTTP request failed: " + urlStr, e);
        } finally {
            // 显式断开连接，释放资源
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * 将参数 Map 构建为 URL 查询字符串（如：name=张三&age=25）。
     * 参数按 key 字典序排序，保证一致性。
     *
     * @param params 参数映射
     * @return 编码后的查询字符串，若无参数则返回空字符串
     */
    private static String buildQueryString(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        List<String> pairs = new ArrayList<>();
        params.entrySet().stream()
                .sorted(Entry.comparingByKey()) // 按 key 排序
                .forEach(entry -> {
                    String key = urlEncode(entry.getKey());
                    String value = urlEncode(entry.getValue());
                    pairs.add(key + "=" + value);
                });
        return String.join("&", pairs);
    }

    /**
     * 构建表单请求体（格式与查询字符串相同）。
     */
    private static String buildFormBody(Map<String, String> params) {
        return buildQueryString(params);
    }

    /**
     * 对字符串进行 URL 编码（UTF-8），并将 '+' 替换为 '%20'（符合 RFC 3986）。
     *
     * @param value 待编码的字符串
     * @return 编码后的字符串，null 输入返回空字符串
     */
    private static String urlEncode(String value) {
        if (value == null) return "";
        return URLEncoder.encode(value, StandardCharsets.UTF_8)
                .replace("+", "%20"); // 修复空格被编码为 '+' 的问题
    }
}