package com.bookSystem.service.impl;

import com.bookSystem.common.BusinessException;
import com.bookSystem.entity.Order;
import com.bookSystem.entity.PaymentRecord;
import com.bookSystem.mapper.PaymentRecordMapper;
import com.bookSystem.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 模拟支付服务实现
 * <p>
 * <b>学生项目方案说明：</b>
 * 由于支付宝/微信支付需要企业资质申请商户号，学生项目无法真实接入。
 * 本实现模拟完整的支付流程，保留真实接口的调用模式，
 * 答辩时可完整演示"选择支付方式 → 生成交易流水 → 支付成功"的全链路。
 * <p>
 * 后续若需真实接入，替换步骤见 {@link PaymentService} 接口文档。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MockPaymentServiceImpl implements PaymentService {

    private final PaymentRecordMapper paymentRecordMapper;

    /** 模拟支付成功率：95%（偶尔失败更真实） */
    private static final double SUCCESS_RATE = 0.95;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentRecord pay(Order order, String paymentMethod) {
        // 1. 生成模拟交易流水号（格式类似真实支付平台）
        String tradeNo = generateTradeNo(paymentMethod);

        // 2. 模拟支付处理
        boolean success = Math.random() < SUCCESS_RATE;

        // 3. 创建支付记录
        PaymentRecord record = new PaymentRecord();
        record.setOrderId(order.getId());
        record.setOrderNo(order.getOrderNo());
        record.setAmount(order.getTotalAmount());
        record.setPaymentMethod(paymentMethod);
        record.setTradeNo(tradeNo);

        if (success) {
            record.setStatus("SUCCESS");
            record.setCompleteTime(LocalDateTime.now());
            log.info("[模拟支付] 支付成功 - 订单号: {}, 流水号: {}, 金额: {}",
                    order.getOrderNo(), tradeNo, order.getTotalAmount());
        } else {
            record.setStatus("FAILED");
            log.warn("[模拟支付] 支付失败 - 订单号: {}, 流水号: {}, 原因: 模拟网络波动",
                    order.getOrderNo(), tradeNo);
        }

        paymentRecordMapper.insert(record);

        if (!success) {
            throw new BusinessException("支付失败，请重试（模拟网络波动）");
        }

        return record;
    }

    @Override
    public PaymentRecord queryStatus(String tradeNo) {
        PaymentRecord record = paymentRecordMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PaymentRecord>()
                        .eq(PaymentRecord::getTradeNo, tradeNo));
        if (record == null) {
            throw new BusinessException("交易流水号不存在");
        }
        return record;
    }

    /**
     * 生成模拟交易流水号
     * <p>
     * 格式：PAY + 日期时间 + 6位随机数
     * 示例：ALIPAY20240101120000123456
     * <p>
     * 真实接入后，此处替换为支付平台返回的 transaction_id。
     */
    private String generateTradeNo(String paymentMethod) {
        String prefix = "ALIPAY".equals(paymentMethod) ? "ALIPAY" : "WECHAT";
        String datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%06d", (int) (Math.random() * 1000000));
        return prefix + datetime + random;
    }

    /**
     * 获取支付方式中文名（供前端展示）
     */
    public static String getPaymentMethodName(String code) {
        if ("ALIPAY".equals(code)) return "支付宝";
        if ("WECHAT".equals(code)) return "微信支付";
        return code;
    }
}
