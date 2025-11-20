package com.hwp.commons.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * 下拉选项数据结构（兼容级联）
 * <p>
 * 前端常用结构：{ label: "...", value: "...", children: [...] }
 *
 * @param <V> 选项值的类型（如 Long, String, Enum）
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@Getter
@Setter
@ToString
public class Option<V> {

    /**
     * 显示标签
     */
    private String label;

    /**
     * 选项值
     */
    private V value;

    /**
     * 子选项（用于级联下拉）
     */
    private List<Option<V>> children;

    public Option() {
    }

    public Option(String label, V value) {
        this.label = label;
        this.value = value;
    }

    public List<Option<V>> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
        }
        return children;
    }

    public Option<V> addChild(Option<V> child) {
        getChildren().add(child);
        return this;
    }

    public boolean isLeaf() {
        return children == null || children.isEmpty();
    }
}
