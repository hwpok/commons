package com.hwpok.commons.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 根据主键操作
 *
 * @param <T> 业务数据
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@Getter
@Setter
@ToString
public class Id<T> {
    private T id;
}
