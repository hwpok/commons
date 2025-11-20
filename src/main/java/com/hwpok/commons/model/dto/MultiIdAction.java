package com.hwpok.commons.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * 多个ID及其对应操作类型的封装
 * <p>
 * 用于表示：对一组实体主键执行不同操作。
 * 用于表示：对一组实体主键执行同一个操作。
 *
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@Getter
@Setter
@ToString
public class MultiIdAction<I, A> {

    /**
     * 实体主键及其对应的操作
     */
    @NotNull(message = "主键与操作映射不能为空")
    private Map<I, A> idToActionMap;
}
