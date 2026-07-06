package com.bookSystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体
 * <p>
 * 记录每一笔支付交易流水，模拟真实支付平台的交易记录。
 * 后续接入真实支付宝/微信支付时，字段可无缝映射到支付回调参数。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_payment_record")
public class PaymentRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单ID */
    private Long orderId;

    /** 订单编号（冗余，方便查询） */
    private String orderNo;

    /** 支付金额 */
    private BigDecimal amount;

    /**
     * 支付方式
     * ALIPAY    - 支付宝
     * WECHAT    - 微信支付
     */
    private String paymentMethod;

    /**
     * 模拟交易流水号（格式：PAY20240101120000123456）
     * 真实接入后改为支付宝/微信返回的 transaction_id
     */
    private String tradeNo;

    /**
     * 支付状态
     * PROCESSING - 处理中（模拟支付中...）
     * SUCCESS    - 支付成功
     * FAILED     - 支付失败
     */
    private String status;

    /** 支付发起时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 支付完成时间 */
    private LocalDateTime completeTime;
}
