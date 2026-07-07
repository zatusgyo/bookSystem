package com.bookSystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookSystem.entity.ShippingTrack;
import com.bookSystem.mapper.ShippingTrackMapper;
import com.bookSystem.service.ShippingTrackService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShippingTrackServiceImpl extends ServiceImpl<ShippingTrackMapper, ShippingTrack> implements ShippingTrackService {

    @Override
    public ShippingTrack addTrack(Long orderId, String orderNo, String location, String description) {
        ShippingTrack track = new ShippingTrack();
        track.setOrderId(orderId);
        track.setOrderNo(orderNo);
        track.setLocation(location);
        track.setDescription(description);
        track.setTrackTime(LocalDateTime.now());

        this.save(track);
        return track;
    }

    @Override
    public List<ShippingTrack> getOrderTracks(Long orderId) {
        LambdaQueryWrapper<ShippingTrack> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShippingTrack::getOrderId, orderId)
               .orderByAsc(ShippingTrack::getTrackTime);
        return this.list(wrapper);
    }
}