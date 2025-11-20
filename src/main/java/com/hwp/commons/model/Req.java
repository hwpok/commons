package com.hwp.commons.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * 通用请求封装类
 *
 * @param <T> 业务数据
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@Getter
@ToString
@SuppressWarnings("unused")
public class Req<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 设备信息，可空（内部调用时通常为 null）
     */
    private final DeviceInfo deviceInfo;

    /**
     * 请求元信息，可空（按业务需要）
     */
    private final ReqMetadata metadata;

    /**
     * 业务数据，不可为空
     */
    @Valid
    @NotNull(message = "请求业务数据(data)不能为空")
    private final T data;

    public Req(T data) {
        this(null, null, data);
    }

    public Req(DeviceInfo deviceInfo, ReqMetadata metadata, T data) {
        this.deviceInfo = deviceInfo;
        this.metadata = metadata;
        this.data = Objects.requireNonNull(data, "data must not be null");
    }
}