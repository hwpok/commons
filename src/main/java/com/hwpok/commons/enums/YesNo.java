package com.hwpok.commons.enums;

/**
 * 是/否 枚举，对应数据库 TINYINT(1) 字段（1=是，0=否）
 *
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@SuppressWarnings("unused")
public enum YesNo implements IEnum<Byte, String> {

    YES((byte) 1, "是"),
    NO((byte) 0, "否");

    private final Byte code;
    private final String name;

    YesNo(Byte code, String name) {
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
     * 将 Boolean 转为编码值，null 视为 false
     */
    public static Byte toCode(Boolean bool) {
        return Boolean.TRUE.equals(bool) ? YES.getCode() : NO.getCode();
    }

    /**
     * 根据编码获取枚举（可选）
     */
    public static YesNo fromCode(Byte code) {
        return YES.code.equals(code) ? YesNo.YES : YesNo.NO;
    }
}
