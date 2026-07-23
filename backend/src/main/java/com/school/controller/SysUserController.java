package com.school.controller;

import com.school.annotation.RequirePermission;
import com.school.annotation.SysLog;
import com.school.common.PageResult;
import com.school.common.Result;
import com.school.dto.UserDTO;
import com.school.service.SysUserService;
import com.school.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@RequirePermission(requireAdmin = true)
public class SysUserController {

    private final SysUserService sysUserService;

    @Operation(summary = "获取用户列表")
    @GetMapping
    public Result<PageResult<UserVO>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) Long roleId) {
        return Result.success(sysUserService.listUsers(page, size, username, realName, roleId));
    }

    @SysLog(value = "新增用户", module = "用户管理")
    @Operation(summary = "新增用户")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody UserDTO dto) {
        sysUserService.addUser(dto);
        return Result.success("新增成功", null);
    }

    @SysLog(value = "更新用户", module = "用户管理")
    @Operation(summary = "更新用户")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UserDTO dto) {
        dto.setId(id);
        sysUserService.updateUser(dto);
        return Result.success("更新成功", null);
    }

    @SysLog(value = "删除用户", module = "用户管理")
    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysUserService.deleteUser(id);
        return Result.success("删除成功", null);
    }

    @SysLog(value = "重置密码", module = "用户管理")
    @Operation(summary = "重置密码")
    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id) {
        sysUserService.resetPassword(id);
        return Result.success("密码已重置为123456", null);
    }

    @SysLog(value = "切换用户状态", module = "用户管理")
    @Operation(summary = "启用/禁用用户")
    @PutMapping("/{id}/toggle-status")
    public Result<Void> toggleStatus(@PathVariable Long id) {
        var user = sysUserService.getById(id);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        user.setStatus(user.getStatus() == 1 ? 0 : 1);
        sysUserService.updateById(user);
        String msg = user.getStatus() == 1 ? "用户已启用" : "用户已禁用";
        return Result.success(msg, null);
    }
}
