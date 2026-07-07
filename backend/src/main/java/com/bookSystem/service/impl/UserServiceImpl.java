package com.bookSystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookSystem.common.BusinessException;
import com.bookSystem.entity.User;
import com.bookSystem.mapper.UserMapper;
import com.bookSystem.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * 用户服务实现
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User register(User user) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }

        // 密码 MD5 加密
        user.setPassword(DigestUtils.md5DigestAsHex(
                user.getPassword().getBytes(StandardCharsets.UTF_8)));
        user.setRole("MEMBER");
        user.setStatus(0);
        user.setMaxBorrowCount(5);
        user.setCurrentBorrowCount(0);

        this.save(user);
        user.setPassword(null); // 不返回密码
        return user;
    }

    @Override
    public User login(String username, String password) {
        String encryptedPassword = DigestUtils.md5DigestAsHex(
                password.getBytes(StandardCharsets.UTF_8));

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username)
               .eq(User::getPassword, encryptedPassword);

        User user = this.getOne(wrapper);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() == 1) {
            throw new BusinessException("账户已被禁用");
        }

        user.setPassword(null); // 不返回密码
        return user;
    }

    @Override
    public boolean updateUserInfo(User user) {
        user.setPassword(null); // 不允许直接修改密码
        return this.updateById(user);
    }
}
