package com.bookSystem.controller;

import com.bookSystem.common.Result;
import com.bookSystem.entity.PaymentRecord;
import com.bookSystem.service.PaymentService;
import com.bookSystem.service.impl.MockPaymentServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 支付控制器
 */
@Tag(name = "支付管理", description = "支付发起、状态查询、确认")
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final MockPaymentServiceImpl mockPaymentService;

    @Operation(summary = "查询支付状态（按交易流水号）")
    @GetMapping("/query")
    public Result<PaymentRecord> queryPayment(@RequestParam String tradeNo) {
        PaymentRecord record = paymentService.queryStatus(tradeNo);
        return Result.success(record);
    }

    @Operation(summary = "确认支付成功（模拟支付回调）")
    @PutMapping("/confirm")
    public Result<PaymentRecord> confirmPayment(@RequestBody Map<String, String> params) {
        String tradeNo = params.get("tradeNo");
        PaymentRecord record = mockPaymentService.confirmPayment(tradeNo);
        return Result.success("支付成功", record);
    }
}