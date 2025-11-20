package com.hwpok.commons.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 请求元数据（Request Metadata)
 *
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@Getter
@ToString
@AllArgsConstructor
public class ReqMetadata implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 全链路追踪 ID
     */
    private final String traceId;
    /**
     * 客户端标识
     */
    private final String clientId;
    /**
     * 请求签名
     */
    private final String sign;
    /**
     * 客户端时间戳（毫秒
     */
    private final Long timestamp;
}
