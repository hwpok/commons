package com.hwpok.commons.enums;

/**
 * 数据库删除标识枚举
 *
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@SuppressWarnings("unused")
public enum DeleteStatus implements IEnum<Byte, String> {
    NORMAL((byte) 0, "正常"),
    DELETED((byte) 1, "已删除");

    private final Byte code;
    private final String name;

    DeleteStatus(Byte code, String name) {
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