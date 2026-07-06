package com.bookSystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookSystem.common.BusinessException;
import com.bookSystem.entity.Book;
import com.bookSystem.entity.Order;
import com.bookSystem.entity.OrderItem;
import com.bookSystem.mapper.BookMapper;
import com.bookSystem.mapper.OrderItemMapper;
import com.bookSystem.mapper.OrderMapper;
import com.bookSystem.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单服务实现
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final BookMapper bookMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderMapper orderMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(Long userId, List<Long> bookIds, List<Integer> quantities,
                             String receiverName, String receiverPhone, String receiverAddress) {
        if (bookIds == null || bookIds.isEmpty()) {
            throw new BusinessException("请选择要购买的图书");
        }
        if (bookIds.size() != quantities.size()) {
            throw new BusinessException("图书与数量不匹配");
        }

        // 生成订单编号
        String orderNo = "ORD" + System.currentTimeMillis();

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        // 计算金额并构建订单项
        for (int i = 0; i < bookIds.size(); i++) {
            Book book = bookMapper.selectById(bookIds.get(i));
            if (book == null || book.getStatus() == 0) {
                throw new BusinessException("图书《" + (book != null ? book.getTitle() : "未知") + "》已下架");
            }
            if (book.getTotalStock() < quantities.get(i)) {
                throw new BusinessException("图书《" + book.getTitle() + "》库存不足");
            }

            BigDecimal subtotal = book.getSalePrice().multiply(BigDecimal.valueOf(quantities.get(i)));
            totalAmount = totalAmount.add(subtotal);

            OrderItem item = new OrderItem();
            item.setBookId(book.getId());
            item.setBookTitle(book.getTitle());
            item.setBookCover(book.getCoverImage());
            item.setQuantity(quantities.get(i));
            item.setUnitPrice(book.getSalePrice());
            item.setSubtotal(subtotal);
            orderItems.add(item);

            // 扣减库存
            book.setTotalStock(book.getTotalStock() - quantities.get(i));
            book.setAvailableStock(book.getAvailableStock() - quantities.get(i));
            bookMapper.updateById(book);
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPaidAmount(BigDecimal.ZERO);
        order.setPaymentStatus("UNPAID");
        order.setOrderStatus("PENDING");
        order.setReceiverName(receiverName);
        order.setReceiverPhone(receiverPhone);
        order.setReceiverAddress(receiverAddress);

        this.save(order);

        // 保存订单项
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }

        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order payOrder(Long orderId, String paymentMethod) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!"UNPAID".equals(order.getPaymentStatus())) {
            throw new BusinessException("订单已支付或已取消");
        }

        // TODO: 接入支付宝/微信支付 SDK
        // 此处模拟支付成功
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus("PAID");
        order.setPaidAmount(order.getTotalAmount());
        order.setPaymentTime(LocalDateTime.now());
        order.setOrderStatus("PROCESSING");

        this.updateById(order);
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order cancelOrder(Long orderId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!"UNPAID".equals(order.getPaymentStatus())) {
            throw new BusinessException("只能取消待付款订单");
        }

        order.setOrderStatus("CANCELLED");
        order.setPaymentStatus("REFUNDED");
        this.updateById(order);

        // 恢复库存
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> items = orderItemMapper.selectList(wrapper);
        for (OrderItem item : items) {
            Book book = bookMapper.selectById(item.getBookId());
            if (book != null) {
                book.setTotalStock(book.getTotalStock() + item.getQuantity());
                book.setAvailableStock(book.getAvailableStock() + item.getQuantity());
                bookMapper.updateById(book);
            }
        }

        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order shipOrder(Long orderId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!"PROCESSING".equals(order.getOrderStatus())) {
            throw new BusinessException("订单状态不正确，无法发货");
        }

        order.setOrderStatus("SHIPPED");
        this.updateById(order);
        return order;
    }

    @Override
    public Object getUserOrders(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId)
               .orderByDesc(Order::getCreateTime);

        Page<Order> pageParam = new Page<>(page != null ? page : 1, size != null ? size : 10);
        return this.page(pageParam, wrapper);
    }
}
