package com.bookSystem.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评论 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long bookId;
    private Long userId;
    private String username;
    private String userAvatar;
    private Integer rating;
    private String content;
    private LocalDateTime createTime;
}
