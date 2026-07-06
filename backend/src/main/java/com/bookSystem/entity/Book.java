package com.bookSystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 图书实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_book")
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** ISBN 编号 */
    private String isbn;

    /** 书名 */
    private String title;

    /** 作者 */
    private String author;

    /** 出版社 */
    private String publisher;

    /** 出版日期 */
    private LocalDate publishDate;

    /** 分类ID */
    private Long categoryId;

    /** 图书简介 */
    private String description;

    /** 封面图片URL */
    private String coverImage;

    /** 总库存数量 */
    private Integer totalStock;

    /** 当前可借数量 */
    private Integer availableStock;

    /** 销售价格 */
    private BigDecimal salePrice;

    /** 借阅价格（元/天） */
    private BigDecimal borrowPricePerDay;

    /** 借阅模式: SINGLE-单人借阅, MULTI-多人共读 */
    private String borrowMode;

    /** 最大共读人数（多人共读模式） */
    private Integer maxCoReadCount;

    /** 图书状态: 0-下架, 1-上架, 2-缺货 */
    private Integer status;

    /** 浏览次数 */
    private Long viewCount;

    /** 借阅次数 */
    private Long borrowCount;

    /** 逻辑删除 */
    @TableLogic
    private Integer isDeleted;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
