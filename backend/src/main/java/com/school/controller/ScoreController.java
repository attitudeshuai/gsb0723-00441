package com.school.controller;

import com.school.annotation.SysLog;
import com.school.common.Result;
import com.school.dto.ScoreInputDTO;
import com.school.entity.Score;
import com.school.service.ScoreService;
import com.school.vo.ScoreStatsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 成绩管理控制器
 */
@Tag(name = "成绩管理")
@RestController
@RequestMapping("/scores")
@RequiredArgsConstructor
public class ScoreController {

    private final ScoreService scoreService;

    @Operation(summary = "获取成绩列表")
    @GetMapping
    public Result<List<Score>> list(
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long courseId) {
        return Result.success(scoreService.listScores(examId, classId, courseId));
    }

    @SysLog(value = "录入成绩", module = "成绩管理")
    @Operation(summary = "录入成绩")
    @PostMapping
    public Result<Void> batchInput(@Valid @RequestBody ScoreInputDTO dto) {
        scoreService.batchInputScores(dto);
        return Result.success("录入成功", null);
    }

    @Operation(summary = "获取成绩统计")
    @GetMapping("/stats")
    public Result<ScoreStatsVO> getStats(
            @RequestParam Long examId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long courseId) {
        return Result.success(scoreService.getScoreStats(examId, classId, courseId));
    }

    @Operation(summary = "获取学生成绩趋势")
    @GetMapping("/trend")
    public Result<Map<String, Object>> getTrend(
            @RequestParam Long studentId,
            @RequestParam(required = false) Long courseId) {
        return Result.success(scoreService.getScoreTrend(studentId, courseId));
    }
}
