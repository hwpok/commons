package com.hwp.commons.enums;

/**
 * 星期枚举
 *
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@SuppressWarnings("unused")
public enum WeekEnum implements IEnum<Byte, String>  {
    Mon((byte) 1, "星期一"),
    Tue((byte) 2, "星期二"),
    Wed((byte) 3, "星期三"),
    Thu((byte) 4, "星期四"),
    Fri((byte) 5, "星期五"),
    Sat((byte) 6, "星期六"),
    Sun((byte) 7, "星期日");

    private final Byte code;
    private final String name;

    WeekEnum(Byte code, String name) {
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

    public boolean isWeekend() {
        return this == Sat || this == Sun;
    }
}
