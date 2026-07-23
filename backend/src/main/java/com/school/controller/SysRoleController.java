package com.school.controller;

import com.school.annotation.RequirePermission;
import com.school.annotation.SysLog;
import com.school.common.Result;
import com.school.entity.SysRole;
import com.school.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@RequirePermission(requireAdmin = true)
public class SysRoleController {

    private final SysRoleService sysRoleService;

    @Operation(summary = "获取角色列表")
    @GetMapping
    public Result<List<SysRole>> list() {
        return Result.success(sysRoleService.listRoles());
    }

    @SysLog(value = "新增角色", module = "角色管理")
    @Operation(summary = "新增角色")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody SysRole role) {
        sysRoleService.save(role);
        return Result.success("新增成功", null);
    }

    @SysLog(value = "更新角色", module = "角色管理")
    @Operation(summary = "更新角色")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody SysRole role) {
        role.setId(id);
        sysRoleService.updateById(role);
        return Result.success("更新成功", null);
    }

    @SysLog(value = "删除角色", module = "角色管理")
    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysRoleService.removeById(id);
        return Result.success("删除成功", null);
    }
}
