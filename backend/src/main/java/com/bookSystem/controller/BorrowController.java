package com.bookSystem.controller;

import com.bookSystem.common.Result;
import com.bookSystem.dto.BorrowBookDTO;
import com.bookSystem.entity.BorrowRecord;
import com.bookSystem.service.BorrowService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    public Result<BorrowRecord> borrowBook(@Valid @RequestBody BorrowBookDTO dto) {
        BorrowRecord record = borrowService.borrowBook(dto.getUserId(), dto.getBookId(), dto.getBorrowMode());
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
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecord::getUserId, userId)
               .orderByDesc(BorrowRecord::getCreateTime);

        Page<BorrowRecord> pageParam = new Page<>(page, size);
        return Result.success(borrowService.page(pageParam, wrapper));
    }
}
