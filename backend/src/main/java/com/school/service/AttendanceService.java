package com.school.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.school.dto.AttendanceInputDTO;
import com.school.entity.Attendance;
import com.school.vo.AttendanceStatsVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 考勤服务接口
 */
public interface AttendanceService extends IService<Attendance> {

    /**
     * 查询考勤列表
     */
    List<Attendance> listAttendance(Long classId, LocalDate attendDate);

    /**
     * 批量录入考勤
     */
    void batchInputAttendance(AttendanceInputDTO dto);

    /**
     * 获取考勤统计
     */
    AttendanceStatsVO getAttendanceStats(Long classId, LocalDate startDate, LocalDate endDate);
}
