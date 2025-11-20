package com.hwpok.commons.exception;



/**
 * 通用业务异常枚举
 *
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@SuppressWarnings("unused")
public enum GlobalErrorCode implements ErrorCode {
    VALIDATION_ERROR("VALIDATION_ERROR", "参数校验失败", "error.validation"),
    AUTH_ERROR("AUTH_ERROR", "认证失败", "error.auth"),
    DATA_NOT_FOUND("DATA_NOT_FOUND", "资源未找到", "error.data.not.found"),
    BUSINESS_RULE_ERROR("BUSINESS_RULE_ERROR", "业务规则冲突", "error.business.rule");

    private final String code;
    private final String message;
    private final String messageKey;

    GlobalErrorCode(String code, String defaultMessage, String messageKey){
        this.code = code;
        this.message = defaultMessage;
        this.messageKey = messageKey;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getMessageKey() {
        return messageKey;
    }
}
