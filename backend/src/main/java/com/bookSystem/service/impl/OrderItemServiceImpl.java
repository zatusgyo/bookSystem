package com.bookSystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookSystem.entity.OrderItem;
import com.bookSystem.mapper.OrderItemMapper;
import com.bookSystem.service.OrderItemService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单项服务实现
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {

    @Override
    public List<OrderItem> getOrderItems(Long orderId) {
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        return this.list(wrapper);
    }
}
