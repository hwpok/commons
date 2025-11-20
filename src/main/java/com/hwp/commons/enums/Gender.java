package com.hwp.commons.enums;

/**
 * 性别枚举
 *
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@SuppressWarnings("unused")
public enum Gender implements IEnum<Byte, String> {
    FEMALE((byte) 0, "女"),
    MALE((byte) 1, "男"),
    UNKNOWN((byte) 2, "未知");

    private final Byte code;
    private final String name;

    Gender(Byte code, String name) {
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
}