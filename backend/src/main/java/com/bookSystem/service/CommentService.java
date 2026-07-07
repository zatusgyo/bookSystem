package com.bookSystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bookSystem.entity.Comment;

import java.util.List;

public interface CommentService extends IService<Comment> {

    /**
     * 添加评论
     */
    Comment addComment(Long bookId, Long userId, String username, Integer rating, String content);

    /**
     * 获取图书评论列表
     */
    List<Comment> getBookComments(Long bookId);

    /**
     * 获取图书平均评分
     */
    Double getAverageRating(Long bookId);
}