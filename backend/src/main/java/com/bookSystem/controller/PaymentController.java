package com.bookSystem.controller;

import com.bookSystem.common.Result;
import com.bookSystem.entity.PaymentRecord;
import com.bookSystem.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 支付控制器
 * <p>
 * 提供支付状态查询接口。支付发起由 OrderController.payOrder 完成。
 */
@Tag(name = "支付管理", description = "支付状态查询")
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "查询支付状态（按交易流水号）")
    @GetMapping("/query")
    public Result<PaymentRecord> queryPayment(@RequestParam String tradeNo) {
        PaymentRecord record = paymentService.queryStatus(tradeNo);
        return Result.success(record);
    }
}
