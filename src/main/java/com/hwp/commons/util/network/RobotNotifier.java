package com.hwp.commons.util.network;


import com.hwp.commons.model.Res;
import com.hwp.commons.util.convert.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 机器人消息通知器（支持钉钉、企业微信）
 *
 * @author wanpeng.hui
 * @since 2020-12-05
 */
@SuppressWarnings("unused")
public class RobotNotifier {

    private static final String DINGTALK_PREFIX = "https://oapi.dingtalk.com";
    private static final String WEIXIN_PREFIX = "https://qyapi.weixin.qq.com";

    private static final String DINGTALK_TEMPLATE = "{\"msgtype\":\"text\",\"text\":{\"content\":\"%s\"}}";
    private static final String WEIXIN_TEMPLATE = "{\"msgtype\":\"text\",\"text\":{\"content\":\"%s\",\"mentioned_list\":[\"@all\"]}}";

    private static final Set<String> DINGTALK_INVALID_CODES = Set.of("300005", "310000"); // 关键字/白名单/IP错误
    private static final Set<String> WEIXIN_INVALID_CODES = Set.of("93000", "48002");     // webhook无效/IP不在白名单

    /**
     * 发送机器人消息
     *
     * @param webhookUrl 钉钉或企业微信的 webhook 地址
     * @param message    消息内容
     * @return Res.data: 1=成功, -1=webhook配置错误（不可重试）, 其他=临时失败（可重试）
     */
    public static Res<Integer> send(String webhookUrl, String message) {
        if (StringUtils.isEmpty(webhookUrl)) {
            return Res.fail("webhookUrl不能为空");
        }
        if (StringUtils.isEmpty(message)) {
            return Res.fail("消息内容不能为空");
        }

        String url = webhookUrl.toLowerCase();
        String jsonBody;

        if (url.startsWith(DINGTALK_PREFIX)) {
            jsonBody = String.format(DINGTALK_TEMPLATE, escapeJson(message));
        } else if (url.startsWith(WEIXIN_PREFIX)) {
            jsonBody = String.format(WEIXIN_TEMPLATE, escapeJson(message));
        } else {
            return Res.fail("不支持的 webhook 类型");
        }

        String response = HttpUtilsV1.doPostJson(url, jsonBody);

        // 使用 JSON 工具解析（假设你有 JsonUtil）
        Map<String, String> respMap = parseJsonResponse(response);
        if (respMap.isEmpty()) {
            return Res.fail("返回结果解析失败: " + response);
        }

        String errCode = String.valueOf(respMap.getOrDefault("errcode", ""));
        String errMsg = StringUtils.trim(String.valueOf(respMap.getOrDefault("errmsg", "未知错误")));

        if ("0".equals(errCode)) {
            return Res.success(1);
        }

        // 判断是否为 webhook 配置错误（不可恢复）
        if ((url.startsWith(DINGTALK_PREFIX) && DINGTALK_INVALID_CODES.contains(errCode)) ||
                (url.startsWith(WEIXIN_PREFIX) && WEIXIN_INVALID_CODES.contains(errCode))) {
            return Res.fail("-1", "Webhook配置错误，请检查URL或权限: " + errMsg);
        }

        return Res.fail("发送失败: " + errMsg);
    }

    /**
     * 简单转义 JSON 字符串中的双引号和反斜杠（防止破坏结构）
     */
    private static String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    /**
     * 解析返回json
     *
     * @return map
     */
    private static Map<String, String> parseJsonResponse(String jsonResStr) {
        Map<String, String> map = new HashMap<>();
        if (jsonResStr == null || jsonResStr.length() < 2) {
            return map;
        }

        String clean = jsonResStr.replaceAll("[\\r\\n]+", "").trim();
        if (!clean.startsWith("{") || !clean.endsWith("}")) {
            return map;
        }

        String content = clean.substring(1, clean.length() - 1);
        if (content.isEmpty()) return map;

        // 关键改进：按 ",\"" 分割（假设每个字段名都以 " 开头）
        // 适用于 {"errcode":"0","errmsg":"ok"} 这种标准格式
        String[] pairs;
        if (content.contains(",\"")) {
            pairs = content.split(",\"");
            for (int i = 1; i < pairs.length; i++) {
                pairs[i] = "\"" + pairs[i]; // 补回被 split 掉的 "
            }
        } else {
            pairs = new String[]{content};
        }

        for (String pair : pairs) {
            pair = pair.trim();
            int colon = pair.indexOf(':');
            if (colon <= 0) continue;

            String keyPart = pair.substring(0, colon).trim();
            String valuePart = pair.substring(colon + 1).trim();

            if (keyPart.startsWith("\"") && keyPart.endsWith("\"")) {
                String key = keyPart.substring(1, keyPart.length() - 1);
                String value = valuePart;
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                map.put(key, value);
            }
        }
        return map;
    }
}