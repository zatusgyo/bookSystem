package com.bookSystem.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应结果
 *
 * @param <T> 数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private Long total;

    /** 当前页码 */
    private Long page;

    /** 每页大小 */
    private Long size;

    /** 数据列表 */
    private List<T> records;

    /**
     * 构建分页结果
     */
    public static <T> PageResult<T> of(Long total, Long page, Long size, List<T> records) {
        return new PageResult<>(total, page, size, records);
    }
}
