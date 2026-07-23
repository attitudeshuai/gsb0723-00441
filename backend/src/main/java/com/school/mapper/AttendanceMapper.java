package com.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.school.entity.Attendance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 考勤Mapper
 */
@Mapper
public interface AttendanceMapper extends BaseMapper<Attendance> {

    /**
     * 查询考勤列表（带学生信息）
     */
    List<Attendance> selectAttendanceList(@Param("classId") Long classId,
                                           @Param("attendDate") LocalDate attendDate);

    /**
     * 考勤统计
     */
    List<Map<String, Object>> selectAttendanceStats(@Param("classId") Long classId,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);

    /**
     * 统计今日出勤率
     */
    Map<String, Object> selectTodayAttendanceRate(@Param("date") LocalDate date);

    /**
     * 统计最近7天考勤趋势
     */
    List<Map<String, Object>> selectWeeklyAttendanceTrend(@Param("startDate") LocalDate startDate,
                                                          @Param("endDate") LocalDate endDate);
}
