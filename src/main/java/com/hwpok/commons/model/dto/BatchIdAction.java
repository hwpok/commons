package com.hwpok.commons.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 批量ID操作指令
 * <p>
 * 用于表示：对一组实体主键执行同一个操作。
 *
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@Getter
@Setter
@ToString
public class BatchIdAction<I, A> {

    /**
     * 实体主键列表
     */
    @NotNull(message = "主键列表不能为空")
    private List<I> ids;

    /**
     * 操作（建议使用业务枚举）
     */
    @NotNull(message = "操作类型不能为空")
    private A action;
}
