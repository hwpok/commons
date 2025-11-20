package com.hwp.commons.model;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * 分页查询封装
 *
 * @param <C> 查询条件 DTO 类型
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@Getter
@Setter
@ToString
@SuppressWarnings("unused")
public class PageQuery<C> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 当前页码，从 1 开始
     */
    private int pageNum = Const.DEFAULT_PAGE_NUM;

    /**
     * 每页记录数
     */
    private int pageSize = Const.DEFAULT_PAGE_SIZE;

    /**
     * 查询条件
     */
    @Valid
    private C condition;

    /**
     * 排序规则，格式示例: "createTime,desc;name,asc"
     * 注意：实际使用时必须通过白名单校验，不可直接拼接 SQL！
     */
    private String sort;

    public PageQuery() {
    }

    public PageQuery(C condition) {
        this.condition = condition;
    }

    public PageQuery(int pageNum, int pageSize, C condition) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.condition = condition;
    }

    /**
     * 获取数据库偏移量 (offset)
     */
    public int getOffset() {
        return Math.max(0, (getPage() - 1) * getSize());
    }

    public int getPage() {
        return Math.max(pageNum, Const.DEFAULT_PAGE_NUM);
    }

    public int getSize() {
        return Math.max(Const.MIN_PAGE_SIZE, Math.min(pageSize, Const.MAX_PAGE_SIZE));
    }

    /**
     * 校验排序字段是否在白名单内
     *
     * @param allowedFieldSet 允许排序的字段白名单
     * @return true-安全, false-有风险
     */
    public boolean isSortSafe(Set<String> allowedFieldSet) {
        // 防御性校验
        if (allowedFieldSet == null) {
            return false;
        }

        // 无排序视为安全
        if (sort == null || sort.isEmpty()) {
            return true;
        }

        String[] rawItems = sort.split(";");
        // 防 DoS：限制排序字段数量
        if (rawItems.length > 5) {
            return false;
        }

        for (String rawItem : rawItems) {
            String item = rawItem.trim();

            // 忽略空项
            if (item.isEmpty()) {
                continue;
            }

            // -1 保留尾部空串（更严谨）
            String[] parts = item.split(",", -1);
            if (parts.length != 2) {
                return false;
            }

            String field = parts[0].trim();
            String sortType = parts[1].trim();
            if (field.isEmpty() || sortType.isEmpty()) {
                return false;
            }

            // 检查排序标识是否正确
            if (!sortType.equalsIgnoreCase("asc")
                    && !sortType.equalsIgnoreCase("desc")) {
                return false;
            }

            // 检查是否包含在安全字段中
            if (!allowedFieldSet.contains(field)) {
                return false;
            }
        }
        return true;
    }


    private static class Const {
        /**
         * 默认当前页
         */
        public static final int DEFAULT_PAGE_NUM = 1;
        /**
         * 默认每页大小
         */
        public static final int DEFAULT_PAGE_SIZE = 10;
        /**
         * 每页最大记录数（防止 OOM）
         */
        public static final int MAX_PAGE_SIZE = 1000;
        /**
         * 每页最小记录数
         */
        public static final int MIN_PAGE_SIZE = 1;
    }
}