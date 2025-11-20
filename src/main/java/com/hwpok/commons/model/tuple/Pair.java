package com.hwpok.commons.model.tuple;

import lombok.Value;

/**
 * 两个元素的元组
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@Value
public class Pair<A, B> {
    A first;
    B second;

    public static <A, B> Pair<A, B> of(A first, B second) {
        return new Pair<>(first, second);
    }
}
