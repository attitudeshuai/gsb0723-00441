package com.school.controller;

import com.school.annotation.RequirePermission;
import com.school.common.PageResult;
import com.school.common.Result;
import com.school.entity.SysLog;
import com.school.service.SysLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

/**
 * 系统日志控制器
 */
@Tag(name = "系统日志")
@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
@RequirePermission(requireAdmin = true)
public class SysLogController {

    private final SysLogService sysLogService;

    @Operation(summary = "获取日志列表")
    @GetMapping
    public Result<PageResult<SysLog>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(sysLogService.listLogs(page, size, module, operation, username, startDate, endDate));
    }

    @Operation(summary = "清理日志")
    @DeleteMapping("/clean")
    public Result<Integer> clean(@RequestParam(defaultValue = "30") Integer days) {
        int count = sysLogService.cleanLogs(days);
        return Result.success("清理完成，共删除 " + count + " 条日志", count);
    }

    @Operation(summary = "导出日志")
    @GetMapping("/export")
    public void export(
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletResponse response) throws IOException {
        sysLogService.exportLogs(module, operation, username, startDate, endDate, response);
    }
}
