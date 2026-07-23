package com.school.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.common.Result;
import com.school.common.ResultCode;
import com.school.entity.SysUser;
import com.school.mapper.SysUserMapper;
import com.school.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT认证拦截器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final SysUserMapper sysUserMapper;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS请求放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = extractToken(request);

        if (!StringUtils.hasText(token)) {
            writeErrorResponse(response, ResultCode.UNAUTHORIZED, "请先登录");
            return false;
        }

        if (!jwtUtil.validateToken(token)) {
            writeErrorResponse(response, ResultCode.TOKEN_INVALID, "Token无效或已过期");
            return false;
        }

        // 将用户信息存入请求属性
        Long userId = jwtUtil.getUserId(token);
        String username = jwtUtil.getUsername(token);
        Long roleId = jwtUtil.getRoleId(token);

        // 兼容旧 token：如果 roleId 为 null，从数据库查询
        if (roleId == null && userId != null) {
            SysUser user = sysUserMapper.selectById(userId);
            if (user != null) {
                roleId = user.getRoleId();
            }
        }

        request.setAttribute("userId", userId);
        request.setAttribute("username", username);
        request.setAttribute("roleId", roleId);

        return true;
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private void writeErrorResponse(HttpServletResponse response, ResultCode resultCode, String message) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        Result<Void> result = Result.error(resultCode.getCode(), message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
