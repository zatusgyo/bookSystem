package com.bookSystem.service;

import com.bookSystem.common.BusinessException;
import com.bookSystem.entity.Book;
import com.bookSystem.entity.Order;
import com.bookSystem.entity.OrderItem;
import com.bookSystem.entity.PaymentRecord;
import com.bookSystem.mapper.BookMapper;
import com.bookSystem.mapper.OrderItemMapper;
import com.bookSystem.mapper.OrderMapper;
import com.bookSystem.service.impl.OrderServiceImpl;
import com.bookSystem.service.ShippingTrackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("订单服务单元测试")
class OrderServiceTest {

    @Mock
    private BookMapper bookMapper;

    @Mock
    private OrderItemMapper orderItemMapper;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private PaymentService paymentService;

    @Mock
    private ShippingTrackService shippingTrackService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Book testBook;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("测试图书");
        testBook.setSalePrice(BigDecimal.valueOf(50.00));
        testBook.setTotalStock(10);
        testBook.setAvailableStock(10);
        testBook.setStatus(1);
        testBook.setCoverImage("cover.jpg");

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setOrderNo("ORD123456");
        testOrder.setUserId(1L);
        testOrder.setTotalAmount(BigDecimal.valueOf(50.00));
        testOrder.setPaidAmount(BigDecimal.ZERO);
        testOrder.setPaymentStatus("UNPAID");
        testOrder.setOrderStatus("PENDING");
        testOrder.setReceiverName("张三");
        testOrder.setReceiverPhone("13800138000");
        testOrder.setReceiverAddress("北京市朝阳区");
    }

    @Test
    @DisplayName("正常创建订单 - 应扣减库存")
    void createOrder_Success() {
        when(bookMapper.selectById(1L)).thenReturn(testBook);
        when(bookMapper.updateById(any(Book.class))).thenReturn(1);
        when(orderMapper.insert(any(Order.class))).thenReturn(1);
        when(orderItemMapper.insert(any(OrderItem.class))).thenReturn(1);

        Order result = orderService.createOrder(1L,
                Collections.singletonList(1L),
                Collections.singletonList(2),
                "张三", "13800138000", "北京市");

        assertThat(result).isNotNull();
        assertThat(result.getOrderNo()).startsWith("ORD");
        assertThat(result.getPaymentStatus()).isEqualTo("UNPAID");
        assertThat(result.getOrderStatus()).isEqualTo("PENDING");
        assertThat(result.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(100.00));
        assertThat(testBook.getTotalStock()).isEqualTo(8);
    }

    @Test
    @DisplayName("空图书列表创建 - 应抛出异常")
    void createOrder_EmptyBooks_ShouldThrow() {
        assertThatThrownBy(() -> orderService.createOrder(1L,
                Collections.emptyList(), Collections.emptyList(),
                "张三", "13800138000", "北京市"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("请选择要购买的图书");
    }

    @Test
    @DisplayName("图书与数量不匹配 - 应抛出异常")
    void createOrder_MismatchLength_ShouldThrow() {
        assertThatThrownBy(() -> orderService.createOrder(1L,
                Collections.singletonList(1L),
                Arrays.asList(1, 2),
                "张三", "13800138000", "北京市"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("数量不匹配");
    }

    @Test
    @DisplayName("库存不足创建 - 应抛出异常")
    void createOrder_StockInsufficient_ShouldThrow() {
        testBook.setTotalStock(1);
        when(bookMapper.selectById(1L)).thenReturn(testBook);

        assertThatThrownBy(() -> orderService.createOrder(1L,
                Collections.singletonList(1L),
                Collections.singletonList(2),
                "张三", "13800138000", "北京市"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("库存不足");
    }

    @Test
    @DisplayName("正常支付 - 应更新订单状态")
    void payOrder_Success() {
        PaymentRecord record = new PaymentRecord();
        record.setTradeNo("ALIPAY123456");
        record.setCompleteTime(LocalDateTime.now());

        when(orderMapper.selectById(1L)).thenReturn(testOrder);
        when(paymentService.pay(any(Order.class), anyString())).thenReturn(record);
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);

        Order result = orderService.payOrder(1L, "ALIPAY");

        assertThat(result.getPaymentStatus()).isEqualTo("PAID");
        assertThat(result.getOrderStatus()).isEqualTo("PROCESSING");
        assertThat(result.getPaymentMethod()).isEqualTo("ALIPAY");
    }

    @Test
    @DisplayName("重复支付 - 应抛出异常")
    void payOrder_AlreadyPaid_ShouldThrow() {
        testOrder.setPaymentStatus("PAID");
        when(orderMapper.selectById(1L)).thenReturn(testOrder);

        assertThatThrownBy(() -> orderService.payOrder(1L, "ALIPAY"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("已支付");
    }

    @Test
    @DisplayName("取消订单 - 应恢复库存")
    void cancelOrder_Success() {
        when(orderMapper.selectById(1L)).thenReturn(testOrder);
        when(orderItemMapper.selectList(any())).thenReturn(Collections.singletonList(
                createOrderItem(1L, 1L, 2)));
        when(bookMapper.selectById(1L)).thenReturn(testBook);
        when(bookMapper.updateById(any(Book.class))).thenReturn(1);
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);

        Order result = orderService.cancelOrder(1L);

        assertThat(result.getOrderStatus()).isEqualTo("CANCELLED");
        assertThat(testBook.getTotalStock()).isEqualTo(12); // 10 + 2
    }

    @Test
    @DisplayName("取消已支付订单 - 应抛出异常")
    void cancelOrder_AlreadyPaid_ShouldThrow() {
        testOrder.setPaymentStatus("PAID");
        when(orderMapper.selectById(1L)).thenReturn(testOrder);

        assertThatThrownBy(() -> orderService.cancelOrder(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("只能取消待付款订单");
    }

    @Test
    @DisplayName("正常发货 - 应更新状态为SHIPPED")
    void shipOrder_Success() {
        testOrder.setOrderStatus("PROCESSING");
        when(orderMapper.selectById(1L)).thenReturn(testOrder);
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);

        Order result = orderService.shipOrder(1L);

        assertThat(result.getOrderStatus()).isEqualTo("SHIPPED");
    }

    @Test
    @DisplayName("非处理中状态发货 - 应抛出异常")
    void shipOrder_WrongStatus_ShouldThrow() {
        testOrder.setOrderStatus("PENDING");
        when(orderMapper.selectById(1L)).thenReturn(testOrder);

        assertThatThrownBy(() -> orderService.shipOrder(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("订单状态不正确");
    }

    private OrderItem createOrderItem(Long orderId, Long bookId, int quantity) {
        OrderItem item = new OrderItem();
        item.setOrderId(orderId);
        item.setBookId(bookId);
        item.setQuantity(quantity);
        item.setUnitPrice(BigDecimal.valueOf(50));
        item.setSubtotal(BigDecimal.valueOf(100));
        return item;
    }
}