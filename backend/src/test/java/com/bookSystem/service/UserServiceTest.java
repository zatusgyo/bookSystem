package com.bookSystem.service;

import com.bookSystem.common.BusinessException;
import com.bookSystem.entity.User;
import com.bookSystem.mapper.UserMapper;
import com.bookSystem.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("用户服务单元测试")
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setStatus(0);
        testUser.setRole("MEMBER");
        testUser.setMaxBorrowCount(5);
        testUser.setCurrentBorrowCount(0);
    }

    @Test
    @DisplayName("正常注册 - 密码应被MD5加密且不返回")
    void register_Success() {
        when(userMapper.count(any())).thenReturn(0L);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        User result = userService.register(testUser);

        assertThat(result).isNotNull();
        assertThat(result.getPassword()).isNull(); // 不返回密码
        assertThat(result.getRole()).isEqualTo("MEMBER");
        assertThat(result.getStatus()).isEqualTo(0);
        assertThat(result.getMaxBorrowCount()).isEqualTo(5);
    }

    @Test
    @DisplayName("注册时密码应被MD5加密")
    void register_PasswordShouldBeEncrypted() {
        when(userMapper.count(any())).thenReturn(0L);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        userService.register(testUser);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).insert(captor.capture());
        User saved = captor.getValue();

        String expected = DigestUtils.md5DigestAsHex("password123".getBytes(StandardCharsets.UTF_8));
        assertThat(saved.getPassword()).isEqualTo(expected);
    }

    @Test
    @DisplayName("重复用户名注册 - 应抛出异常")
    void register_DuplicateUsername_ShouldThrow() {
        when(userMapper.count(any())).thenReturn(1L);

        assertThatThrownBy(() -> userService.register(testUser))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("用户名已存在");
    }

    @Test
    @DisplayName("正确登录 - 应返回用户信息（不含密码）")
    void login_Success() {
        String rawPassword = "password123";
        String encrypted = DigestUtils.md5DigestAsHex(rawPassword.getBytes(StandardCharsets.UTF_8));
        testUser.setPassword(encrypted);

        when(userMapper.selectOne(any())).thenReturn(testUser);

        User result = userService.login("testuser", rawPassword);

        assertThat(result).isNotNull();
        assertThat(result.getPassword()).isNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("错误密码登录 - 应抛出异常")
    void login_WrongPassword_ShouldThrow() {
        when(userMapper.selectOne(any())).thenReturn(null);

        assertThatThrownBy(() -> userService.login("testuser", "wrongpassword"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("用户名或密码错误");
    }

    @Test
    @DisplayName("禁用账户登录 - 应抛出异常")
    void login_DisabledAccount_ShouldThrow() {
        testUser.setStatus(1);
        String encrypted = DigestUtils.md5DigestAsHex("password123".getBytes(StandardCharsets.UTF_8));
        testUser.setPassword(encrypted);
        when(userMapper.selectOne(any())).thenReturn(testUser);

        assertThatThrownBy(() -> userService.login("testuser", "password123"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("已被禁用");
    }

    @Test
    @DisplayName("更新用户信息 - 密码不应被修改")
    void updateUserInfo_ShouldNotUpdatePassword() {
        testUser.setPassword("oldpassword");
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        userService.updateUserInfo(testUser);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userMapper).updateById(captor.capture());
        assertThat(captor.getValue().getPassword()).isNull();
    }
}