package com.bookSystem.controller;

import com.bookSystem.common.Result;
import com.bookSystem.entity.ShippingTrack;
import com.bookSystem.service.ShippingTrackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "物流跟踪", description = "订单物流轨迹查询")
@RestController
@RequestMapping("/api/shipping")
@RequiredArgsConstructor
public class ShippingTrackController {

    private final ShippingTrackService shippingTrackService;

    @Operation(summary = "获取订单物流轨迹")
    @GetMapping("/order/{orderId}")
    public Result<List<ShippingTrack>> getOrderTracks(@PathVariable Long orderId) {
        List<ShippingTrack> tracks = shippingTrackService.getOrderTracks(orderId);
        return Result.success(tracks);
    }
}