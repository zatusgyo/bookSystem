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
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
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

        // 统计借阅中/已逾期的记录数
        LambdaQueryWrapper<BorrowRecord> borrowingWrapper = new LambdaQueryWrapper<>();
        borrowingWrapper.eq(BorrowRecord::getStatus, "BORROWING");
        long activeBorrowCount = borrowService.count(borrowingWrapper);

        LambdaQueryWrapper<BorrowRecord> overdueWrapper = new LambdaQueryWrapper<>();
        overdueWrapper.eq(BorrowRecord::getStatus, "OVERDUE");
        long overdueBorrowCount = borrowService.count(overdueWrapper);

        // 统计各状态订单数
        LambdaQueryWrapper<Order> pendingOrderWrapper = new LambdaQueryWrapper<>();
        pendingOrderWrapper.eq(Order::getOrderStatus, "PENDING");
        long pendingOrderCount = orderService.count(pendingOrderWrapper);

        long totalOrderCount = orderService.count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userCount);
        stats.put("bookCount", bookCount);
        stats.put("activeBorrowCount", activeBorrowCount);
        stats.put("overdueBorrowCount", overdueBorrowCount);
        stats.put("pendingOrderCount", pendingOrderCount);
        stats.put("totalOrderCount", totalOrderCount);
        return Result.success(stats);
    }

    @Operation(summary = "用户列表（分页，支持关键词搜索）")
    @GetMapping("/users")
    public Result<IPage<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        checkAdmin(request);

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                            .or().like(User::getNickname, keyword)
                            .or().like(User::getEmail, keyword)
                            .or().like(User::getPhone, keyword));
        }
        wrapper.orderByDesc(User::getCreateTime);

        Page<User> pageParam = new Page<>(page, size);
        IPage<User> result = userService.page(pageParam, wrapper);
        result.getRecords().forEach(user -> user.setPassword(null));
        return Result.success(result);
    }

    @Operation(summary = "启用/禁用用户")
    @PutMapping("/users/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestParam Integer status,
                                          HttpServletRequest request) {
        checkAdmin(request);

        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setStatus(status);
        userService.updateById(user);
        return Result.success("操作成功");
    }

    @Operation(summary = "图书列表（分页，支持关键词和分类搜索）")
    @GetMapping("/books")
    public Result<IPage<Book>> getBookList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        checkAdmin(request);

        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Book::getTitle, keyword)
                            .or().like(Book::getAuthor, keyword)
                            .or().like(Book::getIsbn, keyword));
        }
        if (categoryId != null) {
            wrapper.eq(Book::getCategoryId, categoryId);
        }
        if (status != null) {
            wrapper.eq(Book::getStatus, status);
        }
        wrapper.orderByDesc(Book::getCreateTime);

        Page<Book> pageParam = new Page<>(page, size);
        IPage<Book> result = bookService.page(pageParam, wrapper);
        return Result.success(result);
    }

    @Operation(summary = "下架/上架图书")
    @PutMapping("/books/{id}/status")
    public Result<Void> updateBookStatus(@PathVariable Long id, @RequestParam Integer status,
                                          HttpServletRequest request) {
        checkAdmin(request);

        Book book = bookService.getById(id);
        if (book == null) {
            return Result.error("图书不存在");
        }
        book.setStatus(status);
        bookService.updateById(book);
        return Result.success("操作成功");
    }

    @Operation(summary = "所有借阅记录（分页，支持状态筛选）")
    @GetMapping("/borrows")
    public Result<IPage<BorrowRecord>> getBorrowList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long userId,
            HttpServletRequest request) {
        checkAdmin(request);

        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(status)) {
            wrapper.eq(BorrowRecord::getStatus, status);
        }
        if (userId != null) {
            wrapper.eq(BorrowRecord::getUserId, userId);
        }
        wrapper.orderByDesc(BorrowRecord::getCreateTime);

        Page<BorrowRecord> pageParam = new Page<>(page, size);
        IPage<BorrowRecord> result = borrowService.page(pageParam, wrapper);
        return Result.success(result);
    }

    @Operation(summary = "所有订单（分页，支持状态筛选）")
    @GetMapping("/orders")
    public Result<IPage<Order>> getOrderList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String orderStatus,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) Long userId,
            HttpServletRequest request) {
        checkAdmin(request);

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(orderStatus)) {
            wrapper.eq(Order::getOrderStatus, orderStatus);
        }
        if (StringUtils.hasText(paymentStatus)) {
            wrapper.eq(Order::getPaymentStatus, paymentStatus);
        }
        if (userId != null) {
            wrapper.eq(Order::getUserId, userId);
        }
        wrapper.orderByDesc(Order::getCreateTime);

        Page<Order> pageParam = new Page<>(page, size);
        IPage<Order> result = orderService.page(pageParam, wrapper);
        return Result.success(result);
    }
}