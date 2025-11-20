package com.hwpok.commons.exception;

import lombok.Getter;

import java.io.Serial;

/**
 * 通用业务异常
 *
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@Getter
@SuppressWarnings("unused")
public class BusinessException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 错误代码
     */
    private final String errorCode;

    private BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    private BusinessException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public static BusinessException of(ErrorCode errorCode) {
        return new BusinessException(errorCode.getCode(), errorCode.getMessage());
    }

    public static BusinessException of(ErrorCode errorCode, Throwable cause) {
        return new BusinessException(errorCode.getCode(), errorCode.getMessage(), cause);
    }
}