package com.bookSystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bookSystem.entity.ShippingTrack;

import java.util.List;

public interface ShippingTrackService extends IService<ShippingTrack> {

    /**
     * 添加物流轨迹
     */
    ShippingTrack addTrack(Long orderId, String orderNo, String location, String description);

    /**
     * 获取订单物流轨迹
     */
    List<ShippingTrack> getOrderTracks(Long orderId);
}