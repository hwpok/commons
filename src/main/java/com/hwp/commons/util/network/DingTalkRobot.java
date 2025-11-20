package com.hwp.commons.util.network;


import com.hwp.commons.model.Res;
import com.hwp.commons.util.convert.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 钉钉机器人消息发送工具
 * @author wanpeng.hui
 * @since 2018-11-28
 */
@SuppressWarnings("unused")
public final class DingTalkRobot {

    private static final String MSG_TYPE_TEXT = "text";
    private static final String MSG_TYPE_MARKDOWN = "markdown";
    private static final String ERROR_CODE_ROBOT_UNAVAILABLE = "-1";

    private DingTalkRobot() {
    }

    /**
     * 发送文本消息
     *
     * @param webhookUrl    钩子地址
     * @param msg           消息内容
     * @param isAtAll       是否@所有人
     * @param mentionedList @用户手机号列表
     * @return 发送结果
     */
    public static Res<Boolean> sendTextMessage(String webhookUrl, String msg, boolean isAtAll, List<String> mentionedList) {
        return sendMessage(webhookUrl, (byte) 0, null, msg, isAtAll, mentionedList);
    }

    /**
     * 发送Markdown消息
     *
     * @param webhookUrl    钩子地址
     * @param title         消息标题
     * @param msg           消息内容
     * @param isAtAll       是否@所有人
     * @param mentionedList @用户手机号列表
     * @return 发送结果
     */
    public static Res<Boolean> sendMarkdownMessage(String webhookUrl, String title, String msg, boolean isAtAll, List<String> mentionedList) {
        return sendMessage(webhookUrl, (byte) 1, title, msg, isAtAll, mentionedList);
    }

    /**
     * 发送消息
     *
     * @param webhookUrl    钩子地址
     * @param msgType       消息类型: 0:text|1:markdown
     * @param title         消息标题（Markdown类型需要）
     * @param msg           消息内容
     * @param isAtAll       是否@所有人
     * @param mentionedList @用户手机号列表
     * @return 发送结果
     */
    private static Res<Boolean> sendMessage(String webhookUrl, Byte msgType, String title, String msg, boolean isAtAll, List<String> mentionedList) {
        // 参数校验
        if (StringUtils.isEmpty(webhookUrl)) {
            return Res.fail("webhookUrl必填");
        }
        if (msgType == null) {
            return Res.fail("消息类型必填");
        }
        if (StringUtils.isEmpty(msg)) {
            return Res.fail("message必填");
        }

        // 组装消息体
        String jsonMessage = msgType == 0 ?
                buildTextMessage(msg, mentionedList, isAtAll) :
                buildMarkdownMessage(title, msg, mentionedList, isAtAll);

        // 发送请求
        String responseJsonStr = HttpUtilsV1.doPostJson(webhookUrl.toLowerCase(), jsonMessage);

        // 解析响应
        Map<String, String> responseMap = parseJsonResponse(responseJsonStr);
        String errCode = responseMap.get("errcode");
        String errMsg = StringUtils.trim(responseMap.get("errmsg"));

        if ("0".equals(errCode)) {
            return Res.success(true);
        }

        // 机器人不可用错误码
        if ("93000".equals(errCode) || "48002".equals(errCode)) {
            return Res.fail(ERROR_CODE_ROBOT_UNAVAILABLE, errMsg);
        }

        return Res.fail(errMsg);
    }

    /**
     * 构建文本消息JSON
     */
    private static String buildTextMessage(String text, List<String> mentionedMobileList, boolean isAtAll) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"msgtype\":\"").append(MSG_TYPE_TEXT).append("\",");
        json.append(buildAtJson(mentionedMobileList, isAtAll));
        json.append("\"text\":{\"content\":\"").append(escapeJson(text)).append("\"}");
        json.append("}");
        return json.toString();
    }

    /**
     * 构建Markdown消息JSON
     */
    private static String buildMarkdownMessage(String title, String text, List<String> mentionedMobileList, boolean isAtAll) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"msgtype\":\"").append(MSG_TYPE_MARKDOWN).append("\",");
        json.append(buildAtJson(mentionedMobileList, isAtAll));
        json.append("\"markdown\":{\"title\":\"").append(escapeJson(title)).append("\",");
        json.append("\"text\":\"").append(escapeJson(text)).append("\"}");
        json.append("}");
        return json.toString();
    }

    /**
     * 构建@信息JSON
     */
    private static String buildAtJson(List<String> mentionedMobileList, boolean isAtAll) {
        StringBuilder json = new StringBuilder();
        json.append("\"at\":{");
        json.append("\"isAtAll\":").append(isAtAll);

        if (mentionedMobileList != null && !mentionedMobileList.isEmpty()) {
            json.append(",\"atMobiles\":[");
            for (int i = 0; i < mentionedMobileList.size(); i++) {
                if (i > 0) json.append(",");
                json.append("\"").append(mentionedMobileList.get(i)).append("\"");
            }
            json.append("]");
        }

        json.append("},");
        return json.toString();
    }

    /**
     * JSON转义处理
     */
    private static String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * 解析JSON响应
     */
    private static Map<String, String> parseJsonResponse(String responseJsonStr) {
        Map<String, String> map = new HashMap<>();

        if (responseJsonStr == null || responseJsonStr.length() < 3) {
            return map;
        }

        // 清理字符串
        String cleaned = responseJsonStr.replaceAll("[\\r\\n]+", "");

        if (cleaned.startsWith("{") && cleaned.endsWith("}")) {
            String content = cleaned.substring(1, cleaned.length() - 1);
            Pattern pattern = Pattern.compile("\"([^\"]+)\":\\s*(\"[^\"]*\"|\\d+)");
            Matcher matcher = pattern.matcher(content);

            while (matcher.find()) {
                String key = matcher.group(1);
                String value = matcher.group(2);
                // 移除值的引号（如果有）
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                map.put(key, value);
            }
        }

        return map;
    }
}
