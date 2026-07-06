package com.bookSystem.controller;

import com.bookSystem.common.Result;
import com.bookSystem.entity.BorrowRecord;
import com.bookSystem.service.BorrowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 借阅控制器
 */
@Tag(name = "借阅管理", description = "图书借阅、归还、续借")
@RestController
@RequestMapping("/api/borrow")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;

    @Operation(summary = "借阅图书")
    @PostMapping
    public Result<BorrowRecord> borrowBook(
            @RequestParam Long userId,
            @RequestParam Long bookId,
            @RequestParam(defaultValue = "SINGLE") String borrowMode) {
        BorrowRecord record = borrowService.borrowBook(userId, bookId, borrowMode);
        return Result.success("借阅成功", record);
    }

    @Operation(summary = "归还图书")
    @PutMapping("/return/{recordId}")
    public Result<BorrowRecord> returnBook(@PathVariable Long recordId) {
        BorrowRecord record = borrowService.returnBook(recordId);
        return Result.success("归还成功", record);
    }

    @Operation(summary = "续借图书")
    @PutMapping("/renew/{recordId}")
    public Result<BorrowRecord> renewBook(@PathVariable Long recordId) {
        BorrowRecord record = borrowService.renewBook(recordId);
        return Result.success("续借成功", record);
    }

    @Operation(summary = "查询用户借阅记录")
    @GetMapping("/user/{userId}")
    public Result<Object> getUserBorrowRecords(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        // 使用分页查询
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<BorrowRecord> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecord::getUserId, userId)
               .orderByDesc(BorrowRecord::getCreateTime);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<BorrowRecord> pageParam =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        return Result.success(borrowService.page(pageParam, wrapper));
    }
}
