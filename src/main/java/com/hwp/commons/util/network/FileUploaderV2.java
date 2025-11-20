package com.hwp.commons.util.network;


import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <p>现代化的文件上传工具类（基于 Java 11+ HttpClient）。</p>
 * <p>提供与旧版 FileUploader 兼容的 API，但内部实现更高效、更健壮。</p>
 * <p>
 * <strong>注意：</strong>本工具类不会关闭传入的 {@code InputStream}，
 * 调用方必须自行确保在上传完成后关闭流，以避免资源泄漏。
 * </p>
 *
 * @author Wanpeng.Hui
 * @since 2020/07/18
 */
public final class FileUploaderV2 {
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .version(HttpClient.Version.HTTP_2) // 优先使用 HTTP/2
            .build();

    private FileUploaderV2() {
    }

    /**
     * 上传文件，并返回一个简单的结果对象。
     *
     * @param para 上传参数（注意：不会关闭 para.getInputStream()）
     * @return UploadResult 包含状态码和响应内容
     */
    public static UploadResult upload(UploadFilePara para) {
        try {
            MultipartData mp = buildMultipartBody(para);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(para.getServerUrl()))
                    .timeout(Duration.ofSeconds(60)) // 上传文件建议更长超时
                    .header("Content-Type", "multipart/form-data; boundary=" + mp.boundary)
                    .header("User-Agent", "ModernFileUploader/1.0")
                    .POST(HttpRequest.BodyPublishers.ofByteArray(mp.body))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );

            return new UploadResult(response.statusCode(), response.body());

        } catch (IOException e) {
            return new UploadResult(-1, "Network error: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new UploadResult(-2, "Upload interrupted");
        } catch (Exception e) {
            return new UploadResult(-3, "Unexpected error: " + e.getMessage());
        }
    }

    private static class MultipartData {
        final byte[] body;
        final String boundary;

        MultipartData(byte[] body, String boundary) {
            this.body = body;
            this.boundary = boundary;
        }
    }

    /**
     * 构建 multipart/form-data 请求体
     */
    private static MultipartData buildMultipartBody(UploadFilePara para) throws IOException {
        String boundary = "----" + UUID.randomUUID().toString().replace("-", "");
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        String mimeType = para.getMimeType();
        if (mimeType == null || mimeType.isEmpty()) {
            mimeType = guessMimeType(para.getOriginFileName());
        }

        writeFilePart(out, boundary, para.getFormFileParamName(), para.getOriginFileName(), para.getInputStream(), mimeType);

        for (Map.Entry<String, Object> entry : para.getParasMap().entrySet()) {
            writeFormField(out, boundary, entry.getKey(), entry.getValue().toString());
        }

        out.write(("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
        return new MultipartData(out.toByteArray(), boundary);
    }

    /**
     * 简单 MIME 类型猜测（基于文件扩展名）
     */
    private static String guessMimeType(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "application/octet-stream";
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0) {
            return "application/octet-stream";
        }
        String ext = filename.substring(dotIndex + 1).toLowerCase();
        return switch (ext) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            case "pdf" -> "application/pdf";
            case "txt" -> "text/plain";
            case "html", "htm" -> "text/html";
            case "xml" -> "application/xml";
            case "json" -> "application/json";
            case "zip" -> "application/zip";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls" -> "application/vnd.ms-excel";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            default -> "application/octet-stream";
        };
    }

    private static void writeFilePart(OutputStream out, String boundary, String name, String filename,
                                      InputStream fileIn, String mimeType) throws IOException {
        out.write(("\r\n--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
        out.write(("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + filename + "\"\r\n").getBytes(StandardCharsets.UTF_8));
        out.write(("Content-Type: " + mimeType + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = fileIn.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
    }

    private static void writeFormField(OutputStream out, String boundary, String name, String value) throws IOException {
        out.write(("\r\n--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
        out.write(("Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n").getBytes(StandardCharsets.UTF_8));
        out.write(value.getBytes(StandardCharsets.UTF_8));
    }


    @Getter
    public static class UploadResult {
        private final int statusCode;
        private final String body;

        public UploadResult(int statusCode, String body) {
            this.statusCode = statusCode;
            this.body = body;
        }

        public boolean isOk() {
            return statusCode >= 200 && statusCode < 300;
        }
    }

    @Getter
    @Setter
    public static class UploadFilePara {
        private String serverUrl;
        private Map<String, Object> parasMap = new LinkedHashMap<>();
        private InputStream inputStream;
        private String cookie; // 当前未使用，保留兼容性
        private String formFileParamName = "file";
        private String originFileName = "uploaded_file";
        private String mimeType; // 可选，若为空则自动猜测
    }
}
