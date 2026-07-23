package com.school.controller;

import com.school.common.Result;
import com.school.dto.ClassDTO;
import com.school.entity.Student;
import com.school.service.SchoolClassService;
import com.school.service.StudentService;
import com.school.vo.ClassVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 班级管理控制器
 */
@Tag(name = "班级管理")
@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
public class SchoolClassController {

    private final SchoolClassService classService;
    private final StudentService studentService;

    @Operation(summary = "获取班级列表")
    @GetMapping
    public Result<List<ClassVO>> list(@RequestParam(required = false) Integer grade) {
        return Result.success(classService.listClasses(grade));
    }

    @Operation(summary = "新增班级")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody ClassDTO dto) {
        classService.addClass(dto);
        return Result.success("新增成功", null);
    }

    @Operation(summary = "更新班级")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ClassDTO dto) {
        dto.setId(id);
        classService.updateClass(dto);
        return Result.success("更新成功", null);
    }

    @Operation(summary = "删除班级")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        classService.deleteClass(id);
        return Result.success("删除成功", null);
    }

    @Operation(summary = "获取班级学生名单")
    @GetMapping("/{id}/students")
    public Result<List<Student>> getStudents(@PathVariable Long id) {
        return Result.success(studentService.getStudentsByClassId(id));
    }
}
