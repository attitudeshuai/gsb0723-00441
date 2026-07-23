package com.school.controller;

import com.school.annotation.SysLog;
import com.school.common.PageResult;
import com.school.common.Result;
import com.school.dto.TeacherDTO;
import com.school.service.TeacherService;
import com.school.vo.TeacherVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 教师管理控制器
 */
@Tag(name = "教师管理")
@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @Operation(summary = "获取教师列表")
    @GetMapping
    public Result<PageResult<TeacherVO>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String subject) {
        return Result.success(teacherService.listTeachers(page, size, name, subject));
    }

    @Operation(summary = "获取全部教师（下拉选择用）")
    @GetMapping("/all")
    public Result<List<TeacherVO>> listAll() {
        return Result.success(teacherService.listAllTeachers());
    }

    @SysLog(value = "新增教师", module = "教师管理")
    @Operation(summary = "新增教师")
    @PostMapping
    public Result<Map<String, String>> add(@Valid @RequestBody TeacherDTO dto) {
        String teacherNo = teacherService.addTeacher(dto);
        return Result.success("新增成功", Map.of("teacherNo", teacherNo));
    }

    @SysLog(value = "更新教师", module = "教师管理")
    @Operation(summary = "更新教师")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody TeacherDTO dto) {
        dto.setId(id);
        teacherService.updateTeacher(dto);
        return Result.success("更新成功", null);
    }

    @SysLog(value = "删除教师", module = "教师管理")
    @Operation(summary = "删除教师")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return Result.success("删除成功", null);
    }
}
