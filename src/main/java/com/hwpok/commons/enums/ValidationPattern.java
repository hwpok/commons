package com.hwpok.commons.enums;

import lombok.Getter;

import java.util.regex.Pattern;

/**
 * 正则表达式枚举（仅用于生成验证提示信息）
 * 注意：部分复杂规则（如日期、IP、URL）不建议用于实际校验，应使用标准API
 *
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@Getter
@SuppressWarnings("unused")
public enum ValidationPattern {

    UPPER("大写字母", "只允许大写字母", "^[A-Z]*$"),
    UPPER_UNDERSCORE("大写字母和下划线", "只允许大写字母、下划线", "^([A-Z]+[_])*[A-Z]+$"),
    LOWER("小写字母", "只允许小写字母", "^[a-z]*$"),
    LOWER_UNDERSCORE("小写字母和下划线", "只允许小写字母、下划线", "^([a-z]+[_])*[a-z]+$"),
    ALPHA("大小写字母", "只允许大小写字母", "^[A-Za-z]*$"),
    ALPHA_UNDERSCORE("大小写字母和下划线", "只允许大小写字母、下划线", "^([A-Za-z]+[_])*[A-Za-z]+$"),
    ALPHANUMERIC("字母数字", "只允许大小写字母、数字", "^[A-Za-z0-9]*$"),
    ALPHANUMERIC_UNDERSCORE("字母数字下划线", "只允许大小写字母、数字、下划线", "^([A-Za-z0-9]+[_])*[A-Za-z0-9]+$"),
    CHINESE("汉字", "只允许输入汉字", "^[\\u4e00-\\u9fa5]+$"),
    ALPHANUMERIC_CHINESE("字母数字汉字", "只允许大小写字母、数字、汉字", "^[A-Za-z0-9\\u4e00-\\u9fa5]+$"),
    ALPHANUMERIC_CHINESE_UNDERSCORE("字母数字汉字下划线", "只允许大小写字母、数字、汉字、下划线", "^([A-Za-z0-9\\u4e00-\\u9fa5]+[_])*[A-Za-z0-9\\u4e00-\\u9fa5]+$"),

    // 整数（仅长度限制，不含小数！）
    INT1("1位整数", "只允许1位整数", "^[0-9]{1}$"),
    INT2("2位整数", "只允许1-2位整数", "^[0-9]{1,2}$"),
    INT4("4位整数", "只允许1-4位整数", "^[0-9]{1,4}$"),
    INT6("6位整数", "只允许1-6位整数", "^[0-9]{1,6}$"),
    INT8("8位整数", "只允许1-8位整数", "^[0-9]{1,8}$"),
    INT10("10位整数", "只允许1-10位整数", "^[0-9]{1,10}$"),
    INT12("12位整数", "只允许1-12位整数", "^[0-9]{1,12}$"),

    // 金额类（业务相关，保留但修正描述）
    NUMERIC_POS_8_2("正数(8整2小)", "最多8位整数、2位小数的正数", "^\\d{1,8}(?:\\.\\d{1,2})?$"),
    NUMERIC_8_2("数字(8整2小)", "最多8位整数、2位小数", "^-?\\d{1,8}(?:\\.\\d{1,2})?$"),

    // 网络类（仅用于提示，实际校验请用标准API）
    IP("IP地址", "IPv4地址格式不正确", "^([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.([0-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}$"),
    EMAIL("电子邮箱", "邮箱格式不正确", "^[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?$"),
    MOBILE("手机号", "中国大陆11位手机号码", "^1[3-9]\\d{9}$"), // 更新号段
    URL("网址", "URL格式不正确", "https?://.+"), // 简化，仅作提示

    // 文件
    IMAGE_FILE("图片文件", "仅支持 jpg/jpeg/gif/png/bmp 格式", "(?i)\\.(jpg|jpeg|png|gif|bmp)$"),
    VIDEO_FILE("视频文件", "仅支持 mp4/avi/3gp 格式", "(?i)\\.(mp4|avi|3gp)$"),

    // 其他
    ID_CARD("身份证号", "15位或18位身份证号码", "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}[\\dXx]$)"),
    USERNAME("用户名", "以字母开头，5-18位字母、数字、下划线", "^[a-zA-Z][a-zA-Z0-9_]{4,17}$"),
    PASSWORD("密码", "6-10位，需包含大小写字母和数字", "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,10}$"),
    QQ("QQ号", "5-10位数字", "^[1-9]\\d{4,9}$"),
    POSTCODE("邮政编码", "6位中国邮政编码", "^[1-9]\\d{5}$");

    private final String name;
    private final String desc;
    private final String reg;
    // 为提高性能预编译
    private final Pattern pattern;

    ValidationPattern(String name, String desc, String reg) {
        this.name = name;
        this.desc = desc;
        this.reg = reg;
        this.pattern = Pattern.compile(reg);
    }

    public static String getHint(String fieldName, ValidationPattern rule) {
        return fieldName + rule.desc;
    }

    /**
     * 使用预编译的 Pattern 匹配字符串（null 安全）
     */
    public boolean matches(String value) {
        return value != null && pattern.matcher(value).matches();
    }
}