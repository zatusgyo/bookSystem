package com.bookSystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bookSystem.entity.Book;

/**
 * 图书服务接口
 */
public interface BookService extends IService<Book> {

    /**
     * 上架图书
     */
    Book addBook(Book book);

    /**
     * 搜索图书
     */
    Object searchBooks(String keyword, Long categoryId, Integer page, Integer size);

    /**
     * 获取图书详情
     */
    Book getBookDetail(Long id);
}
