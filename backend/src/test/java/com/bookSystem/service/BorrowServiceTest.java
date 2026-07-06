package com.bookSystem.service;

import com.bookSystem.common.BusinessException;
import com.bookSystem.entity.BorrowRecord;
import com.bookSystem.entity.User;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * BorrowService 单元测试示例
 * <p>
 * 组员参考此模板编写自己模块的单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("借阅服务单元测试")
class BorrowServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private BorrowRecordMapper borrowRecordMapper;

    @Mock
    private com.bookSystem.mapper.BookMapper bookMapper;

    @InjectMocks
    private BorrowServiceImpl borrowService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setStatus(0);
        testUser.setMaxBorrowCount(5);
        testUser.setCurrentBorrowCount(0);
    }

    @Test
    @DisplayName("正常借阅 - 应成功创建借阅记录")
    void borrowBook_Success() {
        // TODO: 组员根据实际业务逻辑补充完整测试
        // given: 准备测试数据
        // when: 执行被测方法
        // then: 验证结果

        assertThat(testUser.getCurrentBorrowCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("超量借阅 - 应抛出异常")
    void borrowBook_ExceedLimit_ShouldThrow() {
        testUser.setCurrentBorrowCount(5); // 已达到最大借阅量

        // TODO: 组员补充完整测试逻辑
        // assertThatThrownBy(() -> borrowService.borrowBook(...))
        //     .isInstanceOf(BusinessException.class)
        //     .hasMessage("已达到最大借阅数量");
    }

    @Test
    @DisplayName("归还图书 - 应更新状态和库存")
    void returnBook_Success() {
        // TODO: 组员补充完整测试逻辑
    }

    @Test
    @DisplayName("续借超次数 - 应抛出异常")
    void renewBook_ExceedLimit_ShouldThrow() {
        // TODO: 组员补充完整测试逻辑
    }
}
