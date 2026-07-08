package com.bookSystem.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单 VO（含订单项列表）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime paymentTime;
    private String paymentTradeNo;
    private String orderStatus;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String remark;
    private List<OrderItemVO> items;
    private LocalDateTime createTime;
}
