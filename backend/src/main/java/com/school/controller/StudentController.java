package com.school.controller;

import com.school.annotation.SysLog;
import com.school.common.PageResult;
import com.school.common.Result;
import com.school.dto.StudentDTO;
import com.school.entity.Student;
import com.school.service.StudentService;
import com.school.vo.StudentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 学生管理控制器
 */
@Tag(name = "学生管理")
@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @Operation(summary = "获取学生列表")
    @GetMapping
    public Result<PageResult<StudentVO>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status) {
        return Result.success(studentService.listStudents(page, size, classId, name, status));
    }

    @Operation(summary = "获取学生详情")
    @GetMapping("/{id}")
    public Result<StudentVO> detail(@PathVariable Long id) {
        return Result.success(studentService.getStudentDetail(id));
    }

    @SysLog(value = "新增学生", module = "学生管理")
    @Operation(summary = "新增学生")
    @PostMapping
    public Result<Map<String, String>> add(@Valid @RequestBody StudentDTO dto) {
        String studentNo = studentService.addStudent(dto);
        return Result.success("新增成功", Map.of("studentNo", studentNo));
    }

    @SysLog(value = "更新学生", module = "学生管理")
    @Operation(summary = "更新学生")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody StudentDTO dto) {
        dto.setId(id);
        studentService.updateStudent(dto);
        return Result.success("更新成功", null);
    }

    @SysLog(value = "删除学生", module = "学生管理")
    @Operation(summary = "删除学生")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return Result.success("删除成功", null);
    }

    @SysLog(value = "学生转班", module = "学生管理")
    @Operation(summary = "学生转班")
    @PutMapping("/{id}/transfer")
    public Result<Void> transfer(@PathVariable Long id, @RequestParam Long classId) {
        studentService.transferStudent(id, classId);
        return Result.success("转班成功", null);
    }

    @SysLog(value = "批量导入学生", module = "学生管理")
    @Operation(summary = "批量导入学生")
    @PostMapping("/import")
    public Result<Map<String, Object>> importStudents(@RequestParam("file") MultipartFile file) throws IOException {
        return Result.success("导入成功", studentService.importStudents(file));
    }

    @Operation(summary = "导出学生名单")
    @GetMapping("/export")
    public void exportStudents(@RequestParam(required = false) Long classId, HttpServletResponse response) throws IOException {
        studentService.exportStudents(classId, response);
    }
}
