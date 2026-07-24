package com.school.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.school.dto.AttendanceInputDTO;
import com.school.entity.Attendance;
import com.school.entity.SchoolClass;
import com.school.mapper.AttendanceMapper;
import com.school.mapper.SchoolClassMapper;
import com.school.mapper.StudentMapper;
import com.school.service.AttendanceService;
import com.school.vo.AttendanceStatsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 考勤服务实现类
 */
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl extends ServiceImpl<AttendanceMapper, Attendance> implements AttendanceService {

    private final SchoolClassMapper classMapper;
    private final StudentMapper studentMapper;

    @Override
    public List<Attendance> listAttendance(Long classId, LocalDate attendDate) {
        return baseMapper.selectAttendanceList(classId, attendDate);
    }

    @Override
    @Transactional
    public void batchInputAttendance(AttendanceInputDTO dto) {
        for (AttendanceInputDTO.AttendanceRecord record : dto.getRecords()) {
            // 检查是否已存在记录
            LambdaQueryWrapper<Attendance> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Attendance::getStudentId, record.getStudentId())
                    .eq(Attendance::getAttendDate, dto.getAttendDate());

            Attendance existing = getOne(wrapper);
            if (existing != null) {
                existing.setStatus(record.getStatus());
                existing.setRemark(record.getRemark());
                updateById(existing);
            } else {
                Attendance attendance = new Attendance();
                attendance.setStudentId(record.getStudentId());
                attendance.setAttendDate(dto.getAttendDate());
                attendance.setStatus(record.getStatus());
                attendance.setRemark(record.getRemark());
                save(attendance);
            }
        }
    }

    @Override
    public AttendanceStatsVO getAttendanceStats(Long classId, LocalDate startDate, LocalDate endDate) {
        AttendanceStatsVO vo = new AttendanceStatsVO();

        if (classId != null) {
            SchoolClass clazz = classMapper.selectById(classId);
            if (clazz != null) {
                vo.setClassName(clazz.getClassName());
                vo.setStudentCount(clazz.getStudentCount());
            }
        } else {
            vo.setClassName("全部班级");
            // 获取全部学生数
            vo.setStudentCount(studentMapper.selectCount(null).intValue());
        }

        // 计算天数（闭区间，包含起止日期，与SQL BETWEEN语义保持一致）
        long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        vo.setTotalDays((int) days);

        // 获取统计数据
        List<Map<String, Object>> statsData = baseMapper.selectAttendanceStats(classId, startDate, endDate);

        List<AttendanceStatsVO.StudentAttendance> details = new ArrayList<>();
        int totalAttend = 0;
        int totalRecords = 0;

        for (Map<String, Object> data : statsData) {
            AttendanceStatsVO.StudentAttendance sa = new AttendanceStatsVO.StudentAttendance();
            sa.setStudentId(((Number) data.get("studentId")).longValue());
            sa.setStudentName((String) data.get("studentName"));
            sa.setAttendCount(((Number) data.get("attendCount")).intValue());
            sa.setLateCount(((Number) data.get("lateCount")).intValue());
            sa.setLeaveCount(((Number) data.get("leaveCount")).intValue());
            sa.setAbsentCount(((Number) data.get("absentCount")).intValue());

            int total = ((Number) data.get("totalDays")).intValue();
            if (total > 0) {
                BigDecimal rate = BigDecimal.valueOf(sa.getAttendCount())
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
                sa.setRate(rate);
            }

            totalAttend += sa.getAttendCount();
            totalRecords += total;
            details.add(sa);
        }

        vo.setDetails(details);

        // 计算总出勤率
        if (totalRecords > 0) {
            vo.setAttendanceRate(BigDecimal.valueOf(totalAttend)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalRecords), 2, RoundingMode.HALF_UP));
        } else {
            vo.setAttendanceRate(BigDecimal.ZERO);
        }

        return vo;
    }
}
