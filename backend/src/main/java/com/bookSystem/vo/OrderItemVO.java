package com.bookSystem.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单项 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long bookId;
    private String bookTitle;
    private String bookCover;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
