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
 * 支持两种支付流程：
 * <ol>
 *   <li>直接支付：pay() — 立即返回支付结果（95%成功率）</li>
 *   <li>模拟支付：initiatePayment() → 前端展示支付页面 → confirmPayment() — 模拟真实扫码支付流程</li>
 * </ol>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MockPaymentServiceImpl implements PaymentService {

    private final PaymentRecordMapper paymentRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentRecord pay(Order order, String paymentMethod) {
        String tradeNo = generateTradeNo(paymentMethod);
        boolean success = Math.random() < 0.95;

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
            throw new BusinessException("支付失败，请重试");
        }

        return record;
    }

    /**
     * 发起模拟支付（支付中状态）
     * <p>返回支付记录供前端展示支付页面，实际支付结果稍后通过 confirmPayment 确认。
     */
    @Transactional(rollbackFor = Exception.class)
    public PaymentRecord initiatePayment(Order order, String paymentMethod) {
        String tradeNo = generateTradeNo(paymentMethod);

        PaymentRecord record = new PaymentRecord();
        record.setOrderId(order.getId());
        record.setOrderNo(order.getOrderNo());
        record.setAmount(order.getTotalAmount());
        record.setPaymentMethod(paymentMethod);
        record.setTradeNo(tradeNo);
        record.setStatus("PROCESSING");

        paymentRecordMapper.insert(record);
        log.info("[模拟支付] 发起支付 - 订单号: {}, 流水号: {}, 金额: {}",
                order.getOrderNo(), tradeNo, order.getTotalAmount());

        return record;
    }

    /**
     * 确认支付（模拟支付宝回调）
     * <p>前端倒计时结束后调用，将支付状态从 PROCESSING 改为 SUCCESS。
     */
    @Transactional(rollbackFor = Exception.class)
    public PaymentRecord confirmPayment(String tradeNo) {
        PaymentRecord record = paymentRecordMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PaymentRecord>()
                        .eq(PaymentRecord::getTradeNo, tradeNo));

        if (record == null) {
            throw new BusinessException("交易流水号不存在");
        }
        if (!"PROCESSING".equals(record.getStatus())) {
            throw new BusinessException("支付状态异常，当前状态: " + record.getStatus());
        }

        record.setStatus("SUCCESS");
        record.setCompleteTime(LocalDateTime.now());
        paymentRecordMapper.updateById(record);

        log.info("[模拟支付] 支付确认成功 - 订单号: {}, 流水号: {}, 金额: {}",
                record.getOrderNo(), tradeNo, record.getAmount());

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

    private String generateTradeNo(String paymentMethod) {
        String prefix = "ALIPAY".equals(paymentMethod) ? "ALIPAY" : "WECHAT";
        String datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%06d", (int) (Math.random() * 1000000));
        return prefix + datetime + random;
    }

    public static String getPaymentMethodName(String code) {
        if ("ALIPAY".equals(code)) return "支付宝";
        if ("WECHAT".equals(code)) return "微信支付";
        return code;
    }
}