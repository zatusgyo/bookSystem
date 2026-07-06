package com.bookSystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单编号（业务编号，如 ORD202401010001） */
    private String orderNo;

    /** 用户ID */
    private Long userId;

    /** 订单总金额 */
    private BigDecimal totalAmount;

    /** 实付金额 */
    private BigDecimal paidAmount;

    /** 支付方式: ALIPAY-支付宝, WECHAT-微信支付 */
    private String paymentMethod;

    /** 支付状态: UNPAID-待支付, PAID-已支付, REFUNDING-退款中, REFUNDED-已退款 */
    private String paymentStatus;

    /** 支付时间 */
    private LocalDateTime paymentTime;

    /** 支付交易流水号（模拟: PAY20240101..., 真实接入后为支付宝/微信返回的transaction_id） */
    private String paymentTradeNo;

    /** 订单状态: PENDING-待付款, PROCESSING-处理中, SHIPPED-已发货, DELIVERED-已签收, CANCELLED-已取消 */
    private String orderStatus;

    /** 收货人姓名 */
    private String receiverName;

    /** 收货人手机号 */
    private String receiverPhone;

    /** 收货地址 */
    private String receiverAddress;

    /** 备注 */
    private String remark;

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
