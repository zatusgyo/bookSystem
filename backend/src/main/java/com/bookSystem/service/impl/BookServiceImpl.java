package com.bookSystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookSystem.common.BusinessException;
import com.bookSystem.entity.Book;
import com.bookSystem.mapper.BookMapper;
import com.bookSystem.service.BookService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 图书服务实现
 */
@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {

    @Override
    public Book addBook(Book book) {
        book.setAvailableStock(book.getTotalStock());
        book.setStatus(1);
        book.setViewCount(0L);
        book.setBorrowCount(0L);
        this.save(book);
        return book;
    }

    @Override
    public Object searchBooks(String keyword, Long categoryId, Integer page, Integer size) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getStatus, 1); // 只查上架图书

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Book::getTitle, keyword)
                            .or().like(Book::getAuthor, keyword)
                            .or().like(Book::getIsbn, keyword));
        }
        if (categoryId != null) {
            wrapper.eq(Book::getCategoryId, categoryId);
        }

        wrapper.orderByDesc(Book::getCreateTime);

        Page<Book> pageParam = new Page<>(page != null ? page : 1, size != null ? size : 10);
        return this.page(pageParam, wrapper);
    }

    @Override
    public Book getBookDetail(Long id) {
        Book book = this.getById(id);
        if (book == null || book.getStatus() == 0) {
            throw new BusinessException("图书不存在或已下架");
        }
        // 增加浏览次数
        book.setViewCount(book.getViewCount() + 1);
        this.updateById(book);
        return book;
    }
}
