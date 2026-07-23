package com.school.controller;

import com.school.annotation.SysLog;
import com.school.common.PageResult;
import com.school.common.Result;
import com.school.dto.NoticeDTO;
import com.school.entity.Notice;
import com.school.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 通知公告控制器
 */
@Tag(name = "通知公告")
@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "获取公告列表")
    @GetMapping
    public Result<PageResult<Notice>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer type) {
        return Result.success(noticeService.listNotices(page, size, type));
    }

    @SysLog(value = "发布公告", module = "通知公告")
    @Operation(summary = "发布公告")
    @PostMapping
    public Result<Void> publish(@Valid @RequestBody NoticeDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        noticeService.publishNotice(dto, userId);
        return Result.success("发布成功", null);
    }

    @SysLog(value = "更新公告", module = "通知公告")
    @Operation(summary = "更新公告")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody NoticeDTO dto) {
        dto.setId(id);
        noticeService.updateNotice(dto);
        return Result.success("更新成功", null);
    }

    @SysLog(value = "删除公告", module = "通知公告")
    @Operation(summary = "删除公告")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return Result.success("删除成功", null);
    }

    @SysLog(value = "置顶公告", module = "通知公告")
    @Operation(summary = "置顶/取消置顶")
    @PutMapping("/{id}/top")
    public Result<Void> toggleTop(@PathVariable Long id) {
        noticeService.toggleTop(id);
        return Result.success("操作成功", null);
    }
}
