package com.bookSystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 物流轨迹实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_shipping_track")
public class ShippingTrack implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单ID */
    private Long orderId;

    /** 订单编号（冗余） */
    private String orderNo;

    /** 物流节点位置 */
    private String location;

    /** 物流描述 */
    private String description;

    /** 轨迹时间 */
    private LocalDateTime trackTime;
}