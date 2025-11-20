package com.hwpok.commons.util.time;

import java.net.URI;
import java.net.URLConnection;

/**
 * 纯 HTTP 网络时间工具（优化版）
 */
public final class WebTimeUtils {

    private static final String[] TIME_SITES = {
            "https://www.baidu.com",
            "https://www.taobao.com",
            "https://httpbin.org/get"
    };

    private static final int TIMEOUT_MS = 5_000;

    private WebTimeUtils() {
    }

    public static long getCurrentTime() {
        for (String site : TIME_SITES) {
            Long time = getRemoteTime(site);
            if (time != null && time > 0) {
                return time;
            }
        }
        return System.currentTimeMillis(); // 回退
    }

    private static Long getRemoteTime(String uri) {
        try {
            URLConnection conn = URI.create(uri).toURL().openConnection();
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            conn.connect();
            long date = conn.getDate();
            return (date <= 0) ? null : date;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isExpired(long time) {
        return getCurrentTime() > time;
    }
}
