package com.school.controller;

import com.school.annotation.SysLog;
import com.school.common.Result;
import com.school.dto.AttendanceInputDTO;
import com.school.entity.Attendance;
import com.school.service.AttendanceService;
import com.school.vo.AttendanceStatsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 考勤管理控制器
 */
@Tag(name = "考勤管理")
@RestController
@RequestMapping("/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @Operation(summary = "获取考勤列表")
    @GetMapping
    public Result<List<Attendance>> list(
            @RequestParam Long classId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate attendDate) {
        return Result.success(attendanceService.listAttendance(classId, attendDate));
    }

    @SysLog(value = "录入考勤", module = "考勤管理")
    @Operation(summary = "录入考勤")
    @PostMapping
    public Result<Void> batchInput(@Valid @RequestBody AttendanceInputDTO dto) {
        attendanceService.batchInputAttendance(dto);
        return Result.success("录入成功", null);
    }

    @Operation(summary = "获取考勤统计")
    @GetMapping("/stats")
    public Result<AttendanceStatsVO> getStats(
            @RequestParam(required = false) Long classId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(attendanceService.getAttendanceStats(classId, startDate, endDate));
    }
}
