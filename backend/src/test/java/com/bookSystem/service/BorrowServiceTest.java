package com.bookSystem.service;

import com.bookSystem.common.BusinessException;
import com.bookSystem.entity.Book;
import com.bookSystem.entity.BorrowRecord;
import com.bookSystem.entity.User;
import com.bookSystem.mapper.BookMapper;
import com.bookSystem.mapper.BorrowRecordMapper;
import com.bookSystem.mapper.UserMapper;
import com.bookSystem.service.impl.BorrowServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("借阅服务单元测试")
class BorrowServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BorrowRecordMapper borrowRecordMapper;

    @InjectMocks
    private BorrowServiceImpl borrowService;

    private User testUser;
    private Book testBook;
    private BorrowRecord testRecord;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setStatus(0);
        testUser.setMaxBorrowCount(5);
        testUser.setCurrentBorrowCount(0);

        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("测试图书");
        testBook.setStatus(1);
        testBook.setAvailableStock(10);
        testBook.setTotalStock(10);
        testBook.setBorrowCount(0L);
        testBook.setBorrowMode("SINGLE");

        testRecord = new BorrowRecord();
        testRecord.setId(1L);
        testRecord.setBorrowNo("BR123456");
        testRecord.setUserId(1L);
        testRecord.setBookId(1L);
        testRecord.setBorrowMode("SINGLE");
        testRecord.setBorrowDate(LocalDateTime.now().minusDays(5));
        testRecord.setDueDate(LocalDateTime.now().plusDays(25));
        testRecord.setRenewCount(0);
        testRecord.setStatus("BORROWING");
        testRecord.setBorrowFee(BigDecimal.ZERO);
        testRecord.setOverdueFine(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("正常借阅 - 应成功创建借阅记录并更新库存和用户借阅数")
    void borrowBook_Success() {
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(bookMapper.selectById(1L)).thenReturn(testBook);
        when(borrowRecordMapper.insert(any())).thenReturn(1);

        BorrowRecord result = borrowService.borrowBook(1L, 1L, "SINGLE");

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("BORROWING");
        assertThat(result.getBorrowMode()).isEqualTo("SINGLE");

        verify(bookMapper).updateById(testBook);
        verify(userMapper).updateById(testUser);
    }

    @Test
    @DisplayName("超量借阅 - 应抛出异常")
    void borrowBook_ExceedLimit_ShouldThrow() {
        testUser.setCurrentBorrowCount(5);
        when(userMapper.selectById(1L)).thenReturn(testUser);

        assertThatThrownBy(() -> borrowService.borrowBook(1L, 1L, "SINGLE"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("已达到最大借阅数量");
    }

    @Test
    @DisplayName("用户不存在 - 应抛出异常")
    void borrowBook_UserNotFound_ShouldThrow() {
        when(userMapper.selectById(1L)).thenReturn(null);

        assertThatThrownBy(() -> borrowService.borrowBook(1L, 1L, "SINGLE"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("用户不存在");
    }

    @Test
    @DisplayName("用户被禁用 - 应抛出异常")
    void borrowBook_UserDisabled_ShouldThrow() {
        testUser.setStatus(1);
        when(userMapper.selectById(1L)).thenReturn(testUser);

        assertThatThrownBy(() -> borrowService.borrowBook(1L, 1L, "SINGLE"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("已被禁用");
    }

    @Test
    @DisplayName("图书不存在 - 应抛出异常")
    void borrowBook_BookNotFound_ShouldThrow() {
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(bookMapper.selectById(1L)).thenReturn(null);

        assertThatThrownBy(() -> borrowService.borrowBook(1L, 1L, "SINGLE"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("图书不存在");
    }

    @Test
    @DisplayName("图书库存不足 - 应抛出异常")
    void borrowBook_StockEmpty_ShouldThrow() {
        testBook.setAvailableStock(0);
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(bookMapper.selectById(1L)).thenReturn(testBook);

        assertThatThrownBy(() -> borrowService.borrowBook(1L, 1L, "SINGLE"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("库存不足");
    }

    @Test
    @DisplayName("正常归还 - 应更新状态为RETURNED并恢复库存")
    void returnBook_Success() {
        when(borrowRecordMapper.selectById(1L)).thenReturn(testRecord);
        when(bookMapper.selectById(1L)).thenReturn(testBook);
        when(userMapper.selectById(1L)).thenReturn(testUser);
        testUser.setCurrentBorrowCount(1);

        BorrowRecord result = borrowService.returnBook(1L);

        assertThat(result.getStatus()).isEqualTo("RETURNED");
        assertThat(result.getReturnDate()).isNotNull();
        verify(bookMapper).updateById(testBook);
        verify(userMapper).updateById(testUser);
    }

    @Test
    @DisplayName("逾期归还 - 状态应为OVERDUE并计算罚款")
    void returnBook_Overdue_ShouldSetOverdueStatus() {
        testRecord.setDueDate(LocalDateTime.now().minusDays(3));
        when(borrowRecordMapper.selectById(1L)).thenReturn(testRecord);
        when(bookMapper.selectById(1L)).thenReturn(testBook);
        when(userMapper.selectById(1L)).thenReturn(testUser);
        testUser.setCurrentBorrowCount(1);

        BorrowRecord result = borrowService.returnBook(1L);

        assertThat(result.getStatus()).isEqualTo("OVERDUE");
        assertThat(result.getOverdueFine()).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("归还已归还记录 - 应抛出异常")
    void returnBook_AlreadyReturned_ShouldThrow() {
        testRecord.setStatus("RETURNED");
        when(borrowRecordMapper.selectById(1L)).thenReturn(testRecord);

        assertThatThrownBy(() -> borrowService.returnBook(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("已归还");
    }

    @Test
    @DisplayName("正常续借 - 应增加15天")
    void renewBook_Success() {
        LocalDateTime oldDueDate = testRecord.getDueDate();
        when(borrowRecordMapper.selectById(1L)).thenReturn(testRecord);

        BorrowRecord result = borrowService.renewBook(1L);

        assertThat(result.getRenewCount()).isEqualTo(1);
        assertThat(result.getDueDate()).isAfter(oldDueDate);
    }

    @Test
    @DisplayName("续借超次数 - 应抛出异常")
    void renewBook_ExceedLimit_ShouldThrow() {
        testRecord.setRenewCount(2);
        when(borrowRecordMapper.selectById(1L)).thenReturn(testRecord);

        assertThatThrownBy(() -> borrowService.renewBook(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("已达到最大续借次数");
    }

    @Test
    @DisplayName("续借非借阅中记录 - 应抛出异常")
    void renewBook_NotBorrowing_ShouldThrow() {
        testRecord.setStatus("RETURNED");
        when(borrowRecordMapper.selectById(1L)).thenReturn(testRecord);

        assertThatThrownBy(() -> borrowService.renewBook(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("不可续借");
    }
}