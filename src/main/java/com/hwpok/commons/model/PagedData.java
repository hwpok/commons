package com.hwpok.commons.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分页结果封装
 *
 * @param <T> 查询条件 DTO 类型
 * @author wanpeng.hui
 * @since 2020/09/02
 */
@Getter
@Setter
@SuppressWarnings("unused")
@ToString(exclude = {"records", "metadata"})
public class PagedData<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 符合条件记录数
     */
    private Long total = 0L;
    /**
     * 当前页码，从 1 开始
     */
    private Integer pageNum = 1;

    /**
     * 每页记录数
     */
    private Integer pageSize = 10;

    /**
     * 集合数据
     */
    private List<T> records = new ArrayList<>();

    /**
     * 扩展数据
     */
    private Map<String, Object> metadata;

    /**
     * 无参数构造方法
     */
    public PagedData() {
    }


    public PagedData(PageQuery<?> pageQuery, List<T> records, Long total) {
        this(
                pageQuery.getPageNum(),
                pageQuery.getPageSize(),
                records,
                total
        );
    }


    public PagedData(Integer pageNum, Integer pageSize, List<T> records, Long total) {
        setPageNum(pageNum);
        setPageSize(pageSize);
        setTotal(total);
        setRecords(records);
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = (pageNum != null && pageNum > 0) ? pageNum : 1;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = (pageSize != null && pageSize > 0) ? pageSize : 10;
    }

    public void setTotal(Long total) {
        this.total = Math.max(0L, total);
    }

    public void setRecords(List<T> records) {
        this.records = (records != null) ? records : new ArrayList<>();
    }

    public void putMetadata(String key, Object value) {
        if (metadata == null) metadata = new HashMap<>();
        metadata.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <V> V getMetadata(String key) {
        return metadata != null ? (V) metadata.get(key) : null;
    }

}
