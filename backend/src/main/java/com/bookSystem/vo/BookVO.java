package com.bookSystem.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 图书 VO（含分类名称）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private LocalDate publishDate;
    private Long categoryId;
    private String categoryName;
    private String description;
    private String coverImage;
    private Integer totalStock;
    private Integer availableStock;
    private BigDecimal salePrice;
    private BigDecimal borrowPricePerDay;
    private String borrowMode;
    private Integer maxCoReadCount;
    private Integer status;
    private Long viewCount;
    private Long borrowCount;
    private Double avgRating;
    private Integer commentCount;
}
