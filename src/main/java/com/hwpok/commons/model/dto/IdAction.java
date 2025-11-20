package com.hwpok.commons.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 表示对某个实体 ID 执行一个操作。
 * <p>
 * 推荐用法：
 * - I: 主键类型（Long, String 等）
 * - A: 业务操作类型(Byte, 枚举 等)
 *
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@Getter
@Setter
@ToString
public class IdAction<I, A> {
    /**
     * 实体主键
     */
    @NotNull(message = "主键不能为空")
    private I id;

    /**
     * 操作类型（建议配合业务枚举使用）
     */
    @NotNull(message = "操作类型不能为空")
    private A action;
}
