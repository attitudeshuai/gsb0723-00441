package com.school.controller;

import com.school.common.Result;
import com.school.dto.ExamDTO;
import com.school.entity.Exam;
import com.school.service.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 考试管理控制器
 */
@Tag(name = "考试管理")
@RestController
@RequestMapping("/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @Operation(summary = "获取考试列表")
    @GetMapping
    public Result<List<Exam>> list(
            @RequestParam(required = false) Integer grade,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) Integer examType) {
        return Result.success(examService.listExams(grade, semester, examType));
    }

    @Operation(summary = "新增考试")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody ExamDTO dto) {
        examService.addExam(dto);
        return Result.success("新增成功", null);
    }

    @Operation(summary = "更新考试")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ExamDTO dto) {
        dto.setId(id);
        examService.updateExam(dto);
        return Result.success("更新成功", null);
    }

    @Operation(summary = "删除考试")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        examService.deleteExam(id);
        return Result.success("删除成功", null);
    }
}
