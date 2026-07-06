package com.bookSystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bookSystem.entity.BorrowRecord;

/**
 * 借阅服务接口
 */
public interface BorrowService extends IService<BorrowRecord> {

    /**
     * 借阅图书
     */
    BorrowRecord borrowBook(Long userId, Long bookId, String borrowMode);

    /**
     * 归还图书
     */
    BorrowRecord returnBook(Long recordId);

    /**
     * 续借图书
     */
    BorrowRecord renewBook(Long recordId);
}
