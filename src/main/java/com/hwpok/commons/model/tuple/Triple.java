package com.hwpok.commons.model.tuple;

import lombok.Value;

/**
 * 三个元素的元组
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@Value
public class Triple<A, B, C> {
    A first;
    B second;
    C third;

    public static <A, B, C> Triple<A, B, C> of(A first, B second, C third) {
        return new Triple<>(first, second, third);
    }
}