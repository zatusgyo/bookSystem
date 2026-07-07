package com.bookSystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bookSystem.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     */
    User register(User user);

    /**
     * 用户登录
     */
    User login(String username, String password);

    /**
     * 更新用户信息
     */
    boolean updateUserInfo(User user);
}
