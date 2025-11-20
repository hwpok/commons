package com.hwp.commons.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 设备信息
 *
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@Getter
@ToString
@AllArgsConstructor
public class DeviceInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 操作系统名称，例如 "Android"、"iOS"、"Windows"
     */
    private final String os;

    /**
     * 操作系统版本号，例如 "14"（Android）、"17.5"（iOS）
     */
    private final String osVersion;

    /**
     * 设备型号，例如 "iPhone15,2"、"Pixel 7"、"SM-G998B"
     */
    private final String model;

    /**
     * 应用版本号（用户可见），例如 "2.1.0"
     */
    private final String appVersion;

    /**
     * 应用构建号（内部版本号，通常为整数），例如 10203<br>
     * 若未提供则为 null。
     */
    private final Integer buildNumber;
}
