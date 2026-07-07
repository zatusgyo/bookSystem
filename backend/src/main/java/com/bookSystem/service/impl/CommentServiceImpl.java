package com.bookSystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookSystem.entity.Comment;
import com.bookSystem.mapper.CommentMapper;
import com.bookSystem.service.CommentService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Override
    public Comment addComment(Long bookId, Long userId, String username, Integer rating, String content) {
        Comment comment = new Comment();
        comment.setBookId(bookId);
        comment.setUserId(userId);
        comment.setUsername(username);
        comment.setRating(rating);
        comment.setContent(content);

        this.save(comment);
        return comment;
    }

    @Override
    public List<Comment> getBookComments(Long bookId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getBookId, bookId)
               .orderByDesc(Comment::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public Double getAverageRating(Long bookId) {
        List<Comment> comments = getBookComments(bookId);
        if (comments.isEmpty()) {
            return null;
        }

        int sum = comments.stream().mapToInt(Comment::getRating).sum();
        BigDecimal avg = BigDecimal.valueOf((double) sum / comments.size())
                .setScale(1, RoundingMode.HALF_UP);
        return avg.doubleValue();
    }
}