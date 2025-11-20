package com.hwp.commons.model.tuple;

import lombok.Value;

/**
 * 一个元素的元组
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@Value
public class Single<T> {
    T value;

    public static <T> Single<T> with(final T value) {
        return new Single<>(value);
    }
}
