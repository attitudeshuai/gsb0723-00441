package com.school.interceptor;

import com.school.annotation.RequirePermission;
import com.school.common.ResultCode;
import com.school.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

/**
 * 权限拦截器
 * 验证用户是否具有访问接口的权限
 */
@Slf4j
@Component
public class PermissionInterceptor implements HandlerInterceptor {

    /**
     * 管理员角色ID
     */
    private static final Long ADMIN_ROLE_ID = 1L;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 非方法处理器直接放行
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // 获取方法或类上的权限注解
        RequirePermission permission = handlerMethod.getMethodAnnotation(RequirePermission.class);
        if (permission == null) {
            permission = handlerMethod.getBeanType().getAnnotation(RequirePermission.class);
        }

        // 没有权限注解，放行
        if (permission == null) {
            return true;
        }

        // 获取用户信息
        Long userId = (Long) request.getAttribute("userId");
        Long roleId = (Long) request.getAttribute("roleId");

        if (userId == null || roleId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户未登录");
        }

        // 检查是否需要管理员权限
        if (permission.requireAdmin() && !ADMIN_ROLE_ID.equals(roleId)) {
            log.warn("用户 {} 尝试访问需要管理员权限的接口: {}", userId, request.getRequestURI());
            throw new BusinessException(ResultCode.FORBIDDEN, "需要管理员权限");
        }

        // 检查具体权限
        String[] requiredPermissions = permission.value();
        if (requiredPermissions.length > 0) {
            // 获取用户权限列表（从请求属性中获取，由JWT拦截器设置）
            @SuppressWarnings("unchecked")
            List<String> userPermissions = (List<String>) request.getAttribute("permissions");

            if (userPermissions == null || userPermissions.isEmpty()) {
                // 管理员拥有所有权限
                if (ADMIN_ROLE_ID.equals(roleId)) {
                    return true;
                }
                throw new BusinessException(ResultCode.FORBIDDEN, "权限不足");
            }

            boolean hasPermission;
            if (permission.logical()) {
                // AND 逻辑：必须拥有所有权限
                hasPermission = userPermissions.containsAll(Arrays.asList(requiredPermissions));
            } else {
                // OR 逻辑：拥有任一权限即可
                hasPermission = Arrays.stream(requiredPermissions)
                        .anyMatch(userPermissions::contains);
            }

            if (!hasPermission) {
                log.warn("用户 {} 权限不足，需要权限: {}", userId, Arrays.toString(requiredPermissions));
                throw new BusinessException(ResultCode.FORBIDDEN, "权限不足");
            }
        }

        return true;
    }
}
