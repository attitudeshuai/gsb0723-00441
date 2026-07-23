package com.school.service;

import com.school.vo.DashboardVO;

/**
 * 仪表盘服务接口
 */
public interface DashboardService {

    /**
     * 获取首页统计数据
     */
    DashboardVO getStats();
}
