package com.school.service;

import com.school.common.PageResult;
import com.school.entity.SysLog;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

/**
 * 系统日志服务接口
 */
public interface SysLogService {

    /**
     * 分页查询日志
     *
     * @param page      页码
     * @param size      每页数量
     * @param module    模块
     * @param operation 操作描述（用于筛选操作类型，如"新增"、"修改"等）
     * @param username  用户名
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 日志列表
     */
    PageResult<SysLog> listLogs(Integer page, Integer size, String module, String operation,
                                 String username, LocalDate startDate, LocalDate endDate);

    /**
     * 清理指定天数之前的日志
     *
     * @param days 天数
     * @return 清理数量
     */
    int cleanLogs(Integer days);

    /**
     * 导出日志到Excel
     *
     * @param module    模块
     * @param operation 操作类型
     * @param username  用户名
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param response  HTTP响应
     * @throws IOException IO异常
     */
    void exportLogs(String module, String operation, String username,
                     LocalDate startDate, LocalDate endDate, HttpServletResponse response) throws IOException;
}
