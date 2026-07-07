package com.bookSystem.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookSystem.common.BusinessException;
import com.bookSystem.entity.Book;
import com.bookSystem.entity.BorrowRecord;
import com.bookSystem.entity.User;
import com.bookSystem.mapper.BookMapper;
import com.bookSystem.mapper.BorrowRecordMapper;
import com.bookSystem.mapper.UserMapper;
import com.bookSystem.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 借阅服务实现
 */
@Service
@RequiredArgsConstructor
public class BorrowServiceImpl extends ServiceImpl<BorrowRecordMapper, BorrowRecord> implements BorrowService {

    private final BookMapper bookMapper;
    private final UserMapper userMapper;
    private final BorrowRecordMapper borrowRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BorrowRecord borrowBook(Long userId, Long bookId, String borrowMode) {
        // 检查用户
        User user = userMapper.selectById(userId);
        if (user == null || user.getStatus() == 1) {
            throw new BusinessException("用户不存在或已被禁用");
        }
        if (user.getCurrentBorrowCount() >= user.getMaxBorrowCount()) {
            throw new BusinessException("已达到最大借阅数量，请先归还部分图书");
        }

        // 检查图书
        Book book = bookMapper.selectById(bookId);
        if (book == null || book.getStatus() != 1) {
            throw new BusinessException("图书不存在或不可借阅");
        }
        if (book.getAvailableStock() <= 0) {
            throw new BusinessException("图书库存不足");
        }

        // 生成借阅编号
        String borrowNo = "BR" + System.currentTimeMillis();

        // 创建借阅记录
        BorrowRecord record = new BorrowRecord();
        record.setBorrowNo(borrowNo);
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setBorrowMode(borrowMode);
        record.setBorrowDate(LocalDateTime.now());
        record.setDueDate(LocalDateTime.now().plusDays(30)); // 默认借阅30天
        record.setRenewCount(0);
        record.setStatus("BORROWING");
        record.setBorrowFee(BigDecimal.ZERO); // 初期免费借阅
        record.setOverdueFine(BigDecimal.ZERO);

        this.save(record);

        // 更新图书库存
        book.setAvailableStock(book.getAvailableStock() - 1);
        book.setBorrowCount(book.getBorrowCount() + 1);
        bookMapper.updateById(book);

        // 更新用户借阅数
        user.setCurrentBorrowCount(user.getCurrentBorrowCount() + 1);
        userMapper.updateById(user);

        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BorrowRecord returnBook(Long recordId) {
        BorrowRecord record = this.getById(recordId);
        if (record == null || !"BORROWING".equals(record.getStatus())) {
            throw new BusinessException("借阅记录不存在或已归还");
        }

        record.setReturnDate(LocalDateTime.now());
        record.setStatus("RETURNED");

        // 检查是否逾期
        if (LocalDateTime.now().isAfter(record.getDueDate())) {
            record.setStatus("OVERDUE");
            long overdueDays = ChronoUnit.DAYS.between(record.getDueDate(), LocalDateTime.now());
            record.setOverdueFine(BigDecimal.valueOf(overdueDays).multiply(BigDecimal.valueOf(0.5)));
        }

        this.updateById(record);

        // 更新图书库存
        Book book = bookMapper.selectById(record.getBookId());
        if (book != null) {
            book.setAvailableStock(book.getAvailableStock() + 1);
            bookMapper.updateById(book);
        }

        // 更新用户借阅数
        User user = userMapper.selectById(record.getUserId());
        if (user != null && user.getCurrentBorrowCount() > 0) {
            user.setCurrentBorrowCount(user.getCurrentBorrowCount() - 1);
            userMapper.updateById(user);
        }

        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BorrowRecord renewBook(Long recordId) {
        BorrowRecord record = this.getById(recordId);
        if (record == null || !"BORROWING".equals(record.getStatus())) {
            throw new BusinessException("借阅记录不存在或不可续借");
        }
        if (record.getRenewCount() >= 2) {
            throw new BusinessException("已达到最大续借次数（2次）");
        }

        record.setDueDate(record.getDueDate().plusDays(15)); // 续借15天
        record.setRenewCount(record.getRenewCount() + 1);
        this.updateById(record);

        return record;
    }
}
