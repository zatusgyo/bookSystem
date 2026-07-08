package com.bookSystem.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 借阅图书请求 DTO
 */
@Data
public class BorrowBookDTO {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "图书ID不能为空")
    private Long bookId;

    private String borrowMode = "SINGLE";
}
