package com.hwpok.commons.enums;


/**
 * 枚举接口，用于统一获取业务编码与展示名称
 *
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@SuppressWarnings("unused")
public interface IEnum<C, N> {
    C getCode();

    N getName();
}
