package com.bookSystem.service;

import com.bookSystem.entity.Order;
import com.bookSystem.entity.PaymentRecord;

/**
 * 支付服务接口
 * <p>
 * 当前使用模拟实现（MockPaymentServiceImpl），
 * 后续接入真实支付平台时，只需新建一个实现类即可切换。
 *
 * <h3>扩展为真实支付的步骤：</h3>
 * <ol>
 *   <li>申请支付宝商户号（open.alipay.com）或微信商户号（pay.weixin.qq.com）</li>
 *   <li>引入官方 SDK 依赖（见 pom.xml 中已预留注释）</li>
 *   <li>新建 AlipayServiceImpl / WechatPayServiceImpl 实现本接口</li>
 *   <li>在 application.yml 中配置商户密钥</li>
 *   <li>添加支付回调 Controller 处理异步通知</li>
 * </ol>
 */
public interface PaymentService {

    /**
     * 发起支付（模拟）
     *
     * @param order         订单信息
     * @param paymentMethod 支付方式: ALIPAY / WECHAT
     * @return 支付记录（含模拟交易流水号）
     */
    PaymentRecord pay(Order order, String paymentMethod);

    /**
     * 查询支付状态
     *
     * @param tradeNo 交易流水号
     * @return 支付记录
     */
    PaymentRecord queryStatus(String tradeNo);
}
