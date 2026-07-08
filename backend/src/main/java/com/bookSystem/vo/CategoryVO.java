package com.bookSystem.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分类 VO（树形结构）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Long parentId;
    private Integer sortOrder;
    private Integer status;
    private List<CategoryVO> children;
}
