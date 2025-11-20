package com.hwpok.commons.enums;

/**
 * 启用状态枚举（用于表示某项功能或数据是否启用）
 *
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@SuppressWarnings("unused")
public enum EnableStatus implements IEnum<Byte, String> {
    DISABLED((byte) 0, "无效"),
    ENABLED((byte) 1, "有效");

    private final Byte code;
    private final String name;

    EnableStatus(Byte code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public Byte getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * 根据字节编码获取对应的枚举值
     *
     * @param code 状态编码（0 或 1）
     * @return 对应的枚举，若不存在则返回 null
     */
    public static EnableStatus fromCode(byte code) {
        for (EnableStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }

    /**
     * 根据整数编码获取对应的枚举值（兼容 int 类型调用）
     *
     * @param code 状态编码（应为 0 或 1）
     * @return 对应的枚举，若超出 byte 范围或不存在则返回 null
     */
    public static EnableStatus fromCode(int code) {
        if (code < Byte.MIN_VALUE || code > Byte.MAX_VALUE) {
            return null;
        }
        return fromCode((byte) code);
    }

    /**
     * 根据编码获取名称，若编码无效则返回空字符串
     *
     * @param code 状态编码
     * @return 状态名称，如 "有效" / "无效"
     */
    public static String getNameByCode(int code) {
        EnableStatus status = fromCode(code);
        return status != null ? status.getName() : "";
    }

    /**
     * 判断是否为启用状态
     *
     * @param code 状态编码
     * @return true 表示启用（有效）
     */
    public static boolean isEnabled(int code) {
        return ENABLED.getCode() == (byte) code;
    }
}