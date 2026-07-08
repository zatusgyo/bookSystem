package com.bookSystem.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 添加评论请求 DTO
 */
@Data
public class AddCommentDTO {

    @NotNull(message = "图书ID不能为空")
    private Long bookId;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低为1")
    @Max(value = 5, message = "评分最高为5")
    private Integer rating;

    @NotBlank(message = "评论内容不能为空")
    private String content;
}
