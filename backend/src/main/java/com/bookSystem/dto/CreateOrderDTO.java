package com.bookSystem.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 创建订单请求 DTO
 */
@Data
public class CreateOrderDTO {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotEmpty(message = "请选择要购买的图书")
    private List<Long> bookIds;

    @NotEmpty(message = "请填写购买数量")
    private List<Integer> quantities;

    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;

    @NotBlank(message = "收货人手机号不能为空")
    private String receiverPhone;

    @NotBlank(message = "收货地址不能为空")
    private String receiverAddress;

    private String remark;
}
