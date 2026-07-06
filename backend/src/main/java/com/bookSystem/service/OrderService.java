package com.bookSystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bookSystem.entity.Order;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService extends IService<Order> {

    /**
     * 创建订单
     */
    Order createOrder(Long userId, List<Long> bookIds, List<Integer> quantities,
                      String receiverName, String receiverPhone, String receiverAddress);

    /**
     * 支付订单
     */
    Order payOrder(Long orderId, String paymentMethod);

    /**
     * 取消订单
     */
    Order cancelOrder(Long orderId);

    /**
     * 发货
     */
    Order shipOrder(Long orderId);

    /**
     * 查询用户订单列表
     */
    Object getUserOrders(Long userId, Integer page, Integer size);
}
