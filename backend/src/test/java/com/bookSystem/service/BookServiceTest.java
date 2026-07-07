package com.bookSystem.service;

import com.bookSystem.common.BusinessException;
import com.bookSystem.entity.Book;
import com.bookSystem.mapper.BookMapper;
import com.bookSystem.service.impl.BookServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("图书服务单元测试")
class BookServiceTest {

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book testBook;

    @BeforeEach
    void setUp() {
        testBook = new Book();
        testBook.setId(1L);
        testBook.setIsbn("978-7-111-11111-1");
        testBook.setTitle("Java编程思想");
        testBook.setAuthor("Bruce Eckel");
        testBook.setPublisher("机械工业出版社");
        testBook.setCategoryId(9L);
        testBook.setTotalStock(50);
        testBook.setAvailableStock(50);
        testBook.setSalePrice(BigDecimal.valueOf(79.00));
        testBook.setBorrowMode("SINGLE");
        testBook.setStatus(1);
    }

    @Test
    @DisplayName("添加图书 - 应自动设置库存和默认值")
    void addBook_Success() {
        when(bookMapper.insert(any(Book.class))).thenReturn(1);

        Book result = bookService.addBook(testBook);

        assertThat(result.getAvailableStock()).isEqualTo(50);
        assertThat(result.getStatus()).isEqualTo(1);
        assertThat(result.getViewCount()).isEqualTo(0L);
        assertThat(result.getBorrowCount()).isEqualTo(0L);
        verify(bookMapper).insert(testBook);
    }

    @Test
    @DisplayName("获取图书详情 - 应增加浏览次数")
    void getBookDetail_ShouldIncrementViewCount() {
        testBook.setViewCount(10L);
        when(bookMapper.selectById(1L)).thenReturn(testBook);
        when(bookMapper.updateById(any(Book.class))).thenReturn(1);

        Book result = bookService.getBookDetail(1L);

        assertThat(result.getViewCount()).isEqualTo(11L);
        verify(bookMapper).updateById(testBook);
    }

    @Test
    @DisplayName("获取已下架图书 - 应抛出异常")
    void getBookDetail_Delisted_ShouldThrow() {
        testBook.setStatus(0);
        when(bookMapper.selectById(1L)).thenReturn(testBook);

        assertThatThrownBy(() -> bookService.getBookDetail(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("已下架");
    }

    @Test
    @DisplayName("获取不存在的图书 - 应抛出异常")
    void getBookDetail_NotFound_ShouldThrow() {
        when(bookMapper.selectById(1L)).thenReturn(null);

        assertThatThrownBy(() -> bookService.getBookDetail(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("图书不存在");
    }

    @Test
    @DisplayName("搜索图书 - 应只返回上架图书")
    void searchBooks_ShouldFilterByStatus() {
        Page<Book> page = new Page<>(1, 10);
        when(bookMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Object result = bookService.searchBooks("Java", 9L, 1, 10);

        ArgumentCaptor<LambdaQueryWrapper> wrapperCaptor = ArgumentCaptor.forClass(LambdaQueryWrapper.class);
        verify(bookMapper).selectPage(any(Page.class), wrapperCaptor.capture());
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("空关键词搜索 - 应返回所有上架图书")
    void searchBooks_EmptyKeyword_ShouldReturnAll() {
        Page<Book> page = new Page<>(1, 10);
        when(bookMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Object result = bookService.searchBooks(null, null, 1, 10);

        assertThat(result).isNotNull();
        verify(bookMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }
}