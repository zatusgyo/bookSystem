package com.bookSystem.controller;

import com.bookSystem.common.BusinessException;
import com.bookSystem.common.Result;
import com.bookSystem.dto.LoginDTO;
import com.bookSystem.dto.RegisterDTO;
import com.bookSystem.dto.UpdateUserDTO;
import com.bookSystem.entity.User;
import com.bookSystem.service.UserService;
import com.bookSystem.utils.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@Tag(name = "用户管理", description = "用户注册、登录、信息管理")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody RegisterDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());

        User registeredUser = userService.register(user);
        return Result.success("注册成功", registeredUser);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO dto) {
        User user = userService.login(dto.getUsername(), dto.getPassword());
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());

        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("token", token);
        return Result.success("登录成功", data);
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/{id}")
    public Result<User> getUserInfo(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/{id}")
    public Result<Void> updateUserInfo(@PathVariable Long id, @Valid @RequestBody UpdateUserDTO dto) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }

        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }

        userService.updateUserInfo(user);
        return Result.ok("更新成功");
    }

    @Operation(summary = "修改密码")
    @PutMapping("/{id}/password")
    public Result<Void> changePassword(
            @PathVariable Long id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 验证旧密码
        String oldEncrypted = DigestUtils.md5DigestAsHex(
                oldPassword.getBytes(StandardCharsets.UTF_8));
        if (!oldEncrypted.equals(user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        // 更新新密码
        String newEncrypted = DigestUtils.md5DigestAsHex(
                newPassword.getBytes(StandardCharsets.UTF_8));
        user.setPassword(newEncrypted);
        userService.updateById(user);
        return Result.ok("密码修改成功");
    }
}