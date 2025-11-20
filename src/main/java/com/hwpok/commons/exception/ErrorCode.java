package com.hwpok.commons.exception;

/**
 * 通用错误接口
 *
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@SuppressWarnings("unused")
public interface ErrorCode {
    String getCode();
    String getMessage();
    String getMessageKey();
}
