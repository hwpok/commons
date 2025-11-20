package com.hwp.commons.util.network;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * <p>现代化 HTTP 请求工具类（基于 Java 11+ HttpClient）。</p>
 * <p>提供与旧版 HttpUtils 兼容的 API，但内部实现更高效、更简洁。</p>
 *
 * @author wanpeng.hui
 * @since 2018-11-28
 */
public final class HttpUtilsV2 {

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .version(HttpClient.Version.HTTP_2) // 优先使用 HTTP/2
            .build();

    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded; charset=UTF-8";
    public static final String CONTENT_TYPE_JSON = "application/json; charset=UTF-8";

    private HttpUtilsV2() {
    }

    /**
     * 发送 GET 请求。
     */
    public static String doGet(String url, Map<String, String> params) {
        String queryString = buildQueryString(params);
        String fullUrl = url + (queryString.isEmpty() ? "" : "?" + queryString);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        return sendRequest(request);
    }

    /**
     * 发送 POST 表单请求。
     */
    public static String doPostForm(String url, Map<String, String> params) {
        String body = buildFormBody(params);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", CONTENT_TYPE_FORM)
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        return sendRequest(request);
    }

    /**
     * 发送 POST JSON 请求。
     */
    public static String doPostJson(String url, String jsonBody) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", CONTENT_TYPE_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .build();

        return sendRequest(request);
    }

    private static String sendRequest(HttpRequest request) {
        try {
            // send 方法会自动处理连接池和资源
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );

            // HttpClient 默认对 4xx/5xx 不抛异常，直接返回响应体，逻辑更统一
            return response.body();
        } catch (IOException | InterruptedException e) {
            // 恢复中断状态
            Thread.currentThread().interrupt();
            throw new RuntimeException("HTTP request failed: " + request.uri(), e);
        }
    }

    private static String buildQueryString(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        return params.entrySet().stream()
                .sorted(Entry.comparingByKey())
                .map(entry -> urlEncode(entry.getKey()) + "=" + urlEncode(entry.getValue()))
                .collect(Collectors.joining("&"));
    }

    private static String buildFormBody(Map<String, String> params) {
        return buildQueryString(params);
    }

    private static String urlEncode(String value) {
        if (value == null) return "";
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }
}

