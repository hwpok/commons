package com.hwp.commons.util.network;

import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <p>简单的HTTP文件上传工具类。</p>
 *
 * @author Wanpeng.Hui
 * @since 2020/07/18
 */
@SuppressWarnings("unused")
public final class FileUploaderV1 {

    private FileUploaderV1() {
    }

    /**
     * 上传文件，并返回一个简单的结果对象。
     *
     * @param para 上传参数
     * @return UploadResult 包含状态码和响应内容
     */
    public static UploadResult upload(UploadFilePara para) {
        HttpURLConnection connection = null;
        OutputStream out = null;
        InputStream in = null;
        BufferedReader reader = null;

        try {
            String boundary = "----" + UUID.randomUUID().toString().replace("-", "");
            byte[] endBoundary = ("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8);

            URL url = new URL(para.getServerUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);

            // 设置请求头
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            if (para.getCookie() != null && !para.getCookie().trim().isEmpty()) {
                connection.setRequestProperty("Cookie", para.getCookie());
            }

            // 获取输出流，准备写入数据
            out = connection.getOutputStream();

            // 1. 写入文件部分
            writeFilePart(out, boundary, para.getFormFileParamName(), para.getOriginFileName(), para.getInputStream());

            // 2. 写入表单字段部分
            for (Map.Entry<String, Object> entry : para.getParasMap().entrySet()) {
                writeFormField(out, boundary, entry.getKey(), entry.getValue().toString());
            }

            // 3. 写入结束标记
            out.write(endBoundary);
            out.flush();

            // 4. 读取响应
            int statusCode = connection.getResponseCode();
            InputStream responseStream = (statusCode >= 200 && statusCode < 300) ? connection.getInputStream() : connection.getErrorStream();

            StringBuilder response = new StringBuilder();
            if (responseStream != null) {
                reader = new BufferedReader(new InputStreamReader(responseStream, StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }

            return new UploadResult(statusCode, response.toString());

        } catch (Exception e) {
            return new UploadResult(-1, "Upload failed: " + e.getMessage());
        } finally {
            // 5. 安全关闭所有资源
            closeQuietly(reader);
            closeQuietly(in);
            closeQuietly(out);
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static void writeFilePart(OutputStream out, String boundary, String name, String filename, InputStream fileIn) throws IOException {
        out.write(("\r\n--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
        out.write(("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + filename + "\"\r\n").getBytes(StandardCharsets.UTF_8));
        out.write(("Content-Type: application/octet-stream\r\n\r\n").getBytes(StandardCharsets.UTF_8));

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

    private static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // 静默关闭
            }
        }
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
        private String cookie;
        private String formFileParamName = "file";
        private String originFileName = "originFileName";
    }
}
