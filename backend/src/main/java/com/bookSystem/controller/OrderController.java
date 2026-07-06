package com.bookSystem.controller;

import com.bookSystem.common.Result;
import com.bookSystem.entity.Order;
import com.bookSystem.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "创建订单")
    @PostMapping
    public Result<Order> createOrder(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        @SuppressWarnings("unchecked")
        List<Long> bookIds = ((List<Integer>) params.get("bookIds")).stream()
                .map(Long::valueOf).collect(java.util.stream.Collectors.toList());
        @SuppressWarnings("unchecked")
        List<Integer> quantities = (List<Integer>) params.get("quantities");
        String receiverName = (String) params.get("receiverName");
        String receiverPhone = (String) params.get("receiverPhone");
        String receiverAddress = (String) params.get("receiverAddress");

        Order order = orderService.createOrder(userId, bookIds, quantities,
                receiverName, receiverPhone, receiverAddress);
        return Result.success("订单创建成功", order);
    }

    @Operation(summary = "支付订单")
    @PutMapping("/pay/{orderId}")
    public Result<Order> payOrder(@PathVariable Long orderId,
                                  @RequestParam(defaultValue = "ALIPAY") String paymentMethod) {
        Order order = orderService.payOrder(orderId, paymentMethod);
        return Result.success("支付成功", order);
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

    @Operation(summary = "查询订单详情")
    @GetMapping("/{orderId}")
    public Result<Order> getOrderDetail(@PathVariable Long orderId) {
        Order order = orderService.getById(orderId);
        return Result.success(order);
    }
}
