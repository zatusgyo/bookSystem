package com.bookSystem.controller;

import com.bookSystem.common.Result;
import com.bookSystem.dto.CreateOrderDTO;
import com.bookSystem.entity.Order;
import com.bookSystem.entity.OrderItem;
import com.bookSystem.entity.PaymentRecord;
import com.bookSystem.service.OrderItemService;
import com.bookSystem.service.OrderService;
import com.bookSystem.service.impl.MockPaymentServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 */
@Tag(name = "订单管理", description = "订单创建、支付、物流")
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MockPaymentServiceImpl mockPaymentService;
    private final OrderItemService orderItemService;

    @Operation(summary = "创建订单")
    @PostMapping
    public Result<Order> createOrder(@Valid @RequestBody CreateOrderDTO dto) {
        Order order = orderService.createOrder(dto.getUserId(), dto.getBookIds(), dto.getQuantities(),
                dto.getReceiverName(), dto.getReceiverPhone(), dto.getReceiverAddress());
        return Result.success("订单创建成功", order);
    }

    @Operation(summary = "支付订单")
    @PutMapping("/pay/{orderId}")
    public Result<Order> payOrder(@PathVariable Long orderId,
                                  @RequestParam(defaultValue = "ALIPAY") String paymentMethod) {
        Order order = orderService.payOrder(orderId, paymentMethod);
        return Result.success("支付成功", order);
    }

    @Operation(summary = "发起模拟支付（获取交易流水号）")
    @PostMapping("/pay/{orderId}/initiate")
    public Result<Map<String, Object>> initiatePayment(@PathVariable Long orderId,
                                                        @RequestParam(defaultValue = "ALIPAY") String paymentMethod) {
        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        PaymentRecord record = mockPaymentService.initiatePayment(order, paymentMethod);

        Map<String, Object> data = new HashMap<>();
        data.put("tradeNo", record.getTradeNo());
        data.put("orderNo", order.getOrderNo());
        data.put("amount", order.getTotalAmount());
        data.put("paymentMethod", paymentMethod);
        data.put("orderId", orderId);
        return Result.success(data);
    }

    @Operation(summary = "取消订单")
    @PutMapping("/cancel/{orderId}")
    public Result<Order> cancelOrder(@PathVariable Long orderId) {
        Order order = orderService.cancelOrder(orderId);
        return Result.success("订单已取消", order);
    }

    @Operation(summary = "发货")
    @PutMapping("/ship/{orderId}")
    public Result<Order> shipOrder(@PathVariable Long orderId) {
        Order order = orderService.shipOrder(orderId);
        return Result.success("发货成功", order);
    }

    @Operation(summary = "查询用户订单")
    @GetMapping("/user/{userId}")
    public Result<Object> getUserOrders(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Object result = orderService.getUserOrders(userId, page, size);
        return Result.success(result);
    }

    @Operation(summary = "查询订单详情（含订单项）")
    @GetMapping("/{orderId}")
    public Result<Map<String, Object>> getOrderDetail(@PathVariable Long orderId) {
        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        List<OrderItem> items = orderItemService.getOrderItems(orderId);

        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        data.put("items", items);
        return Result.success(data);
    }

    @Operation(summary = "查询订单项列表")
    @GetMapping("/{orderId}/items")
    public Result<List<OrderItem>> getOrderItems(@PathVariable Long orderId) {
        List<OrderItem> items = orderItemService.getOrderItems(orderId);
        return Result.success(items);
    }
}
