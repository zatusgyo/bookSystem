package com.bookSystem.controller;

import com.bookSystem.common.Result;
import com.bookSystem.entity.Comment;
import com.bookSystem.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "图书评论", description = "图书评论评分")
@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "添加评论")
    @PostMapping
    public Result<Comment> addComment(
            @RequestParam Long bookId,
            @RequestParam Long userId,
            @RequestParam String username,
            @RequestParam Integer rating,
            @RequestParam String content) {
        Comment comment = commentService.addComment(bookId, userId, username, rating, content);
        return Result.success("评论发布成功", comment);
    }

    @Operation(summary = "获取图书评论列表")
    @GetMapping("/book/{bookId}")
    public Result<List<Comment>> getBookComments(@PathVariable Long bookId) {
        List<Comment> comments = commentService.getBookComments(bookId);
        return Result.success(comments);
    }

    @Operation(summary = "获取图书平均评分")
    @GetMapping("/rating/{bookId}")
    public Result<Double> getAverageRating(@PathVariable Long bookId) {
        Double rating = commentService.getAverageRating(bookId);
        return Result.success(rating);
    }
}