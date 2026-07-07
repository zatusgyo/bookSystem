package com.bookSystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 图书评论实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 图书ID */
    private Long bookId;

    /** 用户ID */
    private Long userId;

    /** 用户名（冗余） */
    private String username;

    /** 评分（1-5） */
    private Integer rating;

    /** 评论内容 */
    private String content;

    /** 逻辑删除 */
    @TableLogic
    private Integer isDeleted;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}