package com.bookSystem.dto;

import lombok.Data;

/**
 * 图书搜索请求 DTO
 */
@Data
public class BookSearchDTO {

    private String keyword;

    private Long categoryId;

    private Integer page = 1;

    private Integer size = 10;
}
