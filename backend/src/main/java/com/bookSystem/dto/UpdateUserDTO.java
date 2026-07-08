package com.bookSystem.dto;

import lombok.Data;

/**
 * 更新用户信息请求 DTO
 */
@Data
public class UpdateUserDTO {

    private String nickname;

    private String email;

    private String phone;

    private String avatar;
}
