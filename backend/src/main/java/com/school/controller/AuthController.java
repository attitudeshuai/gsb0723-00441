package com.school.controller;

import com.school.common.Result;
import com.school.dto.LoginDTO;
import com.school.service.AuthService;
import com.school.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success("登录成功", authService.login(dto));
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/info")
    public Result<LoginVO.UserInfo> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(authService.getUserInfo(userId));
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success("登出成功", null);
    }

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<Void> changePassword(HttpServletRequest request,
                                        @RequestParam String oldPassword,
                                        @RequestParam String newPassword) {
        Long userId = (Long) request.getAttribute("userId");
        authService.changePassword(userId, oldPassword, newPassword);
        return Result.success("密码修改成功", null);
    }
}
