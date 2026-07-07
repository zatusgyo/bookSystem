package com.bookSystem.controller;

import com.bookSystem.common.Result;
import com.bookSystem.entity.Book;
import com.bookSystem.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 图书控制器
 */
@Tag(name = "图书管理", description = "图书的增删改查、搜索")
@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @Operation(summary = "添加图书")
    @PostMapping
    public Result<Book> addBook(@RequestBody Book book) {
        Book savedBook = bookService.addBook(book);
        return Result.success("添加成功", savedBook);
    }

    @Operation(summary = "搜索图书")
    @GetMapping("/search")
    public Result<Object> searchBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Object result = bookService.searchBooks(keyword, categoryId, page, size);
        return Result.success(result);
    }

    @Operation(summary = "获取图书详情")
    @GetMapping("/{id}")
    public Result<Book> getBookDetail(@PathVariable Long id) {
        Book book = bookService.getBookDetail(id);
        return Result.success(book);
    }

    @Operation(summary = "更新图书信息")
    @PutMapping("/{id}")
    public Result<Void> updateBook(@PathVariable Long id, @RequestBody Book book) {
        book.setId(id);
        bookService.updateById(book);
        return Result.ok("更新成功");
    }

    @Operation(summary = "下架图书")
    @DeleteMapping("/{id}")
    public Result<Void> deleteBook(@PathVariable Long id) {
        Book book = bookService.getById(id);
        if (book != null) {
            book.setStatus(0);
            bookService.updateById(book);
        }
        return Result.ok("下架成功");
    }
}
