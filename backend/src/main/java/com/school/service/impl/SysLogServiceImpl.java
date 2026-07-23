package com.school.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.school.common.PageResult;
import com.school.entity.SysLog;
import com.school.mapper.SysLogMapper;
import com.school.mapper.SysUserMapper;
import com.school.service.SysLogService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 系统日志服务实现
 */
@Service
@RequiredArgsConstructor
public class SysLogServiceImpl implements SysLogService {

    private final SysLogMapper sysLogMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    public PageResult<SysLog> listLogs(Integer page, Integer size, String module, String operation,
                                        String username, LocalDate startDate, LocalDate endDate) {
        Page<SysLog> pageParam = new Page<>(page, size);

        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();

        // 模块筛选
        if (StringUtils.hasText(module)) {
            wrapper.eq(SysLog::getModule, module);
        }

        // 操作类型筛选 - 使用like查询匹配操作描述
        if (StringUtils.hasText(operation)) {
            wrapper.like(SysLog::getOperation, operation);
        }

        // 用户名筛选（需要先查用户ID）
        if (StringUtils.hasText(username)) {
            var user = sysUserMapper.selectOne(
                    new LambdaQueryWrapper<com.school.entity.SysUser>()
                            .eq(com.school.entity.SysUser::getUsername, username)
            );
            if (user != null) {
                wrapper.eq(SysLog::getUserId, user.getId());
            } else {
                // 用户不存在，返回空结果
                return new PageResult<>(0L, null);
            }
        }

        // 日期范围筛选
        if (startDate != null) {
            wrapper.ge(SysLog::getCreateTime, LocalDateTime.of(startDate, LocalTime.MIN));
        }
        if (endDate != null) {
            wrapper.le(SysLog::getCreateTime, LocalDateTime.of(endDate, LocalTime.MAX));
        }

        // 按创建时间倒序
        wrapper.orderByDesc(SysLog::getCreateTime);

        Page<SysLog> result = sysLogMapper.selectPage(pageParam, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    @Override
    public int cleanLogs(Integer days) {
        if (days == null || days < 1) {
            days = 30; // 默认清理30天前的日志
        }

        LocalDateTime threshold = LocalDateTime.now().minusDays(days);
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.lt(SysLog::getCreateTime, threshold);

        return sysLogMapper.delete(wrapper);
    }

    @Override
    public void exportLogs(String module, String operation, String username,
                            LocalDate startDate, LocalDate endDate, HttpServletResponse response) throws IOException {
        // 查询符合条件的日志（不限制数量）
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(module)) {
            wrapper.eq(SysLog::getModule, module);
        }
        if (StringUtils.hasText(operation)) {
            wrapper.like(SysLog::getOperation, operation);
        }
        if (StringUtils.hasText(username)) {
            var user = sysUserMapper.selectOne(
                    new LambdaQueryWrapper<com.school.entity.SysUser>()
                            .eq(com.school.entity.SysUser::getUsername, username)
            );
            if (user != null) {
                wrapper.eq(SysLog::getUserId, user.getId());
            } else {
                // 用户不存在，返回空数据
                writeEmptyCsv(response);
                return;
            }
        }
        if (startDate != null) {
            wrapper.ge(SysLog::getCreateTime, LocalDateTime.of(startDate, LocalTime.MIN));
        }
        if (endDate != null) {
            wrapper.le(SysLog::getCreateTime, LocalDateTime.of(endDate, LocalTime.MAX));
        }

        wrapper.orderByDesc(SysLog::getCreateTime);

        // 使用Page来限制最多导出10000条
        Page<SysLog> pageParam = new Page<>(1, 10000);
        Page<SysLog> result = sysLogMapper.selectPage(pageParam, wrapper);
        List<SysLog> logs = result.getRecords();
        writeCsv(response, logs);
    }

    /**
     * 写入CSV文件
     */
    private void writeCsv(HttpServletResponse response, List<SysLog> logs) throws IOException {
        response.setContentType("text/csv;charset=UTF-8");
        String fileName = URLEncoder.encode("操作日志_" + java.time.LocalDate.now() + ".csv", StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        try (PrintWriter writer = response.getWriter()) {
            // 写入BOM以支持Excel正确识别UTF-8
            writer.print("\uFEFF");

            // 写入表头
            writer.println("ID,操作人,操作模块,操作类型,操作描述,请求方法,IP地址,耗时(ms),状态,操作时间");

            // 写入数据
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (SysLog log : logs) {
                writer.printf("%d,%s,%s,%s,%s,%s,%s,%d,%s,%s%n",
                        log.getId(),
                        escapeCsv(log.getUsername()),
                        escapeCsv(log.getModule()),
                        escapeCsv(log.getOperation()),
                        escapeCsv(log.getOperation()),
                        escapeCsv(log.getMethod()),
                        log.getIp(),
                        log.getCostTime(),
                        log.getStatus() == 1 ? "成功" : "失败",
                        log.getCreateTime() != null ? log.getCreateTime().format(formatter) : ""
                );
            }
            writer.flush();
        }
    }

    /**
     * 写入空CSV文件
     */
    private void writeEmptyCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv;charset=UTF-8");
        String fileName = URLEncoder.encode("操作日志_" + java.time.LocalDate.now() + ".csv", StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        try (PrintWriter writer = response.getWriter()) {
            writer.print("\uFEFF");
            writer.println("ID,操作人,操作模块,操作类型,操作描述,请求方法,IP地址,耗时(ms),状态,操作时间");
            writer.flush();
        }
    }

    /**
     * 转义CSV特殊字符
     */
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        // 如果包含逗号、引号或换行符，用引号包裹并转义内部引号
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
