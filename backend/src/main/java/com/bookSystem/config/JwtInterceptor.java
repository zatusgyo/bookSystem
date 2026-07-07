package com.bookSystem.config;

import com.bookSystem.common.BusinessException;
import com.bookSystem.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JWT 认证拦截器
 * 验证请求头中的 Authorization Bearer token
 */
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // OPTIONS 请求放行（跨域预请求）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = extractToken(request);

        // 需要认证的接口必须有 Token
        if (token == null) {
            throw new BusinessException(401, "未授权，请先登录");
        }

        if (!jwtUtils.isTokenValid(token)) {
            throw new BusinessException(401, "Token 已过期，请重新登录");
        }

        // 将用户ID存入 request，方便后续使用
        Long userId = jwtUtils.getUserIdFromToken(token);
        request.setAttribute("userId", userId);

        return true;
    }

    /**
     * 从请求头提取 Token
     * 格式：Authorization: Bearer <token>
     */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}