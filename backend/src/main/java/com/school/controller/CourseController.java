package com.school.controller;

import com.school.annotation.SysLog;
import com.school.common.Result;
import com.school.entity.Course;
import com.school.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程管理控制器
 */
@Tag(name = "课程管理")
@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "获取课程列表")
    @GetMapping
    public Result<List<Course>> list() {
        return Result.success(courseService.listCourses());
    }

    @SysLog(value = "新增课程", module = "课程管理")
    @Operation(summary = "新增课程")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody Course course) {
        courseService.save(course);
        return Result.success("新增成功", null);
    }

    @SysLog(value = "更新课程", module = "课程管理")
    @Operation(summary = "更新课程")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody Course course) {
        course.setId(id);
        courseService.updateById(course);
        return Result.success("更新成功", null);
    }

    @SysLog(value = "删除课程", module = "课程管理")
    @Operation(summary = "删除课程")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        courseService.removeById(id);
        return Result.success("删除成功", null);
    }
}
