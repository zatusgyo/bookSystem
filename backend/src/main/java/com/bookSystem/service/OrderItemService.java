package com.bookSystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bookSystem.entity.OrderItem;

import java.util.List;

/**
 * 订单项服务接口
 */
public interface OrderItemService extends IService<OrderItem> {

    /**
     * 获取订单的所有订单项
     */
    List<OrderItem> getOrderItems(Long orderId);
}
