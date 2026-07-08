package com.bookSystem.controller;

import com.bookSystem.common.BusinessException;
import com.bookSystem.common.Result;
import com.bookSystem.entity.Book;
import com.bookSystem.entity.BorrowRecord;
import com.bookSystem.entity.Order;
import com.bookSystem.entity.User;
import com.bookSystem.service.BookService;
import com.bookSystem.service.BorrowService;
import com.bookSystem.service.OrderService;
import com.bookSystem.service.UserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "管理员后台", description = "数据统计、用户管理、图书管理")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final BookService bookService;
    private final BorrowService borrowService;
    private final OrderService orderService;

    /**
     * 校验当前请求是否为管理员
     */
    private void checkAdmin(HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (role == null || !"ADMIN".equals(role)) {
            throw new BusinessException(403, "无管理员权限");
        }
    }

    @Operation(summary = "获取首页统计数据")
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats(HttpServletRequest request) {
        checkAdmin(request);

        long userCount = userService.count();
        long bookCount = bookService.count();
        long borrowCount = borrowService.count();
        long orderCount = orderService.count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userCount);
        stats.put("bookCount", bookCount);
        stats.put("borrowCount", borrowCount);
        stats.put("orderCount", orderCount);
        return Result.success(stats);
    }

    @Operation(summary = "用户列表（分页）")
    @GetMapping("/users")
    public Result<IPage<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        checkAdmin(request);

        Page<User> pageParam = new Page<>(page, size);
        IPage<User> result = userService.page(pageParam);
        result.getRecords().forEach(user -> user.setPassword(null));
        return Result.success(result);
    }

    @Operation(summary = "启用/禁用用户")
    @PutMapping("/users/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestParam Integer status,
                                          HttpServletRequest request) {
        checkAdmin(request);

        User user = userService.getById(id);
        if (user != null) {
            user.setStatus(status);
            userService.updateById(user);
        }
        return Result.success("操作成功");
    }

    @Operation(summary = "图书列表（分页）")
    @GetMapping("/books")
    public Result<IPage<Book>> getBookList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        checkAdmin(request);

        Page<Book> pageParam = new Page<>(page, size);
        IPage<Book> result = bookService.page(pageParam);
        return Result.success(result);
    }

    @Operation(summary = "下架/上架图书")
    @PutMapping("/books/{id}/status")
    public Result<Void> updateBookStatus(@PathVariable Long id, @RequestParam Integer status,
                                          HttpServletRequest request) {
        checkAdmin(request);

        Book book = bookService.getById(id);
        if (book != null) {
            book.setStatus(status);
            bookService.updateById(book);
        }
        return Result.success("操作成功");
    }

    @Operation(summary = "所有借阅记录（分页）")
    @GetMapping("/borrows")
    public Result<IPage<BorrowRecord>> getBorrowList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        checkAdmin(request);

        Page<BorrowRecord> pageParam = new Page<>(page, size);
        IPage<BorrowRecord> result = borrowService.page(pageParam);
        return Result.success(result);
    }

    @Operation(summary = "所有订单（分页）")
    @GetMapping("/orders")
    public Result<IPage<Order>> getOrderList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        checkAdmin(request);

        Page<Order> pageParam = new Page<>(page, size);
        IPage<Order> result = orderService.page(pageParam);
        return Result.success(result);
    }
}