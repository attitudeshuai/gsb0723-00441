package com.school.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.school.entity.Notice;
import com.school.entity.SchoolClass;
import com.school.entity.Student;
import com.school.entity.Teacher;
import com.school.mapper.AttendanceMapper;
import com.school.mapper.NoticeMapper;
import com.school.mapper.SchoolClassMapper;
import com.school.mapper.ScoreMapper;
import com.school.mapper.StudentMapper;
import com.school.mapper.TeacherMapper;
import com.school.service.DashboardService;
import com.school.vo.DashboardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仪表盘服务实现类
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final StudentMapper studentMapper;
    private final TeacherMapper teacherMapper;
    private final SchoolClassMapper classMapper;
    private final AttendanceMapper attendanceMapper;
    private final NoticeMapper noticeMapper;
    private final ScoreMapper scoreMapper;

    @Override
    public DashboardVO getStats() {
        DashboardVO vo = new DashboardVO();
        LocalDate today = LocalDate.now();
        LocalDate lastMonth = today.minusMonths(1);

        // 学生总数
        LambdaQueryWrapper<Student> studentWrapper = new LambdaQueryWrapper<>();
        studentWrapper.eq(Student::getStatus, 1);
        Long currentStudentCount = studentMapper.selectCount(studentWrapper);
        vo.setStudentCount(currentStudentCount);

        // 教师总数
        LambdaQueryWrapper<Teacher> teacherWrapper = new LambdaQueryWrapper<>();
        teacherWrapper.eq(Teacher::getStatus, 1);
        Long currentTeacherCount = teacherMapper.selectCount(teacherWrapper);
        vo.setTeacherCount(currentTeacherCount);

        // 班级总数
        LambdaQueryWrapper<SchoolClass> classWrapper = new LambdaQueryWrapper<>();
        vo.setClassCount(classMapper.selectCount(classWrapper));

        // 今日出勤率
        Map<String, Object> attendanceData = attendanceMapper.selectTodayAttendanceRate(today);
        BigDecimal todayRate = BigDecimal.ZERO;
        if (attendanceData != null) {
            long totalStudents = ((Number) attendanceData.get("totalStudents")).longValue();
            long attendCount = ((Number) attendanceData.get("attendCount")).longValue();
            if (totalStudents > 0) {
                todayRate = BigDecimal.valueOf(attendCount)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(totalStudents), 2, RoundingMode.HALF_UP);
            }
        }
        vo.setTodayAttendanceRate(todayRate);

        // 昨日出勤率（计算趋势）
        Map<String, Object> yesterdayData = attendanceMapper.selectTodayAttendanceRate(today.minusDays(1));
        if (yesterdayData != null) {
            long totalStudents = ((Number) yesterdayData.get("totalStudents")).longValue();
            long attendCount = ((Number) yesterdayData.get("attendCount")).longValue();
            if (totalStudents > 0) {
                BigDecimal yesterdayRate = BigDecimal.valueOf(attendCount)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(totalStudents), 2, RoundingMode.HALF_UP);
                vo.setAttendanceTrend(todayRate.subtract(yesterdayRate));
            } else {
                vo.setAttendanceTrend(BigDecimal.ZERO);
            }
        } else {
            vo.setAttendanceTrend(BigDecimal.ZERO);
        }

        // 学生趋势（本月新增学生数/上月学生数 * 100）
        LambdaQueryWrapper<Student> newStudentWrapper = new LambdaQueryWrapper<>();
        newStudentWrapper.eq(Student::getStatus, 1)
                .ge(Student::getCreateTime, lastMonth.atStartOfDay());
        Long newStudentCount = studentMapper.selectCount(newStudentWrapper);
        if (currentStudentCount > newStudentCount && newStudentCount > 0) {
            BigDecimal trend = BigDecimal.valueOf(newStudentCount)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(currentStudentCount - newStudentCount), 1, RoundingMode.HALF_UP);
            vo.setStudentTrend(trend);
        } else {
            vo.setStudentTrend(BigDecimal.ZERO);
        }

        // 教师趋势（本月新增教师数）
        LambdaQueryWrapper<Teacher> newTeacherWrapper = new LambdaQueryWrapper<>();
        newTeacherWrapper.eq(Teacher::getStatus, 1)
                .ge(Teacher::getCreateTime, lastMonth.atStartOfDay());
        Long newTeacherCount = teacherMapper.selectCount(newTeacherWrapper);
        vo.setTeacherTrend(newTeacherCount.intValue());

        // 成绩分布
        List<Map<String, Object>> scoreDistData = scoreMapper.selectScoreDistribution();
        List<DashboardVO.ScoreDistributionItem> scoreDistribution = new ArrayList<>();
        if (scoreDistData != null && !scoreDistData.isEmpty()) {
            Map<String, Object> dist = scoreDistData.get(0);
            if (dist != null) {
                addScoreItem(scoreDistribution, "优秀 (90-100)", dist.get("excellent"));
                addScoreItem(scoreDistribution, "良好 (80-89)", dist.get("good"));
                addScoreItem(scoreDistribution, "中等 (70-79)", dist.get("medium"));
                addScoreItem(scoreDistribution, "及格 (60-69)", dist.get("pass"));
                addScoreItem(scoreDistribution, "不及格 (<60)", dist.get("fail"));
            }
        }
        vo.setScoreDistribution(scoreDistribution);

        // 考勤趋势（最近7天）
        LocalDate weekAgo = today.minusDays(6);
        List<Map<String, Object>> attendanceTrendData = attendanceMapper.selectWeeklyAttendanceTrend(weekAgo, today);
        List<DashboardVO.AttendanceTrendItem> trendList = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd");

        // 创建日期到数据的映射
        Map<LocalDate, Map<String, Object>> trendMap = new HashMap<>();
        if (attendanceTrendData != null) {
            for (Map<String, Object> item : attendanceTrendData) {
                Object dateObj = item.get("attendDate");
                if (dateObj instanceof LocalDate) {
                    trendMap.put((LocalDate) dateObj, item);
                } else if (dateObj instanceof java.sql.Date) {
                    trendMap.put(((java.sql.Date) dateObj).toLocalDate(), item);
                }
            }
        }

        // 填充7天数据
        for (int i = 0; i < 7; i++) {
            LocalDate date = weekAgo.plusDays(i);
            DashboardVO.AttendanceTrendItem item = new DashboardVO.AttendanceTrendItem();
            item.setDate(date.format(dateFormatter));

            Map<String, Object> dayData = trendMap.get(date);
            if (dayData != null) {
                long total = ((Number) dayData.get("totalStudents")).longValue();
                long attend = ((Number) dayData.get("attendCount")).longValue();
                if (total > 0) {
                    item.setRate(BigDecimal.valueOf(attend)
                            .multiply(BigDecimal.valueOf(100))
                            .divide(BigDecimal.valueOf(total), 1, RoundingMode.HALF_UP));
                } else {
                    item.setRate(BigDecimal.ZERO);
                }
            } else {
                item.setRate(BigDecimal.ZERO);
            }
            trendList.add(item);
        }
        vo.setAttendanceTrendData(trendList);

        // 最近公告
        LambdaQueryWrapper<Notice> noticeWrapper = new LambdaQueryWrapper<>();
        noticeWrapper.eq(Notice::getStatus, 1)
                .orderByDesc(Notice::getIsTop)
                .orderByDesc(Notice::getPublishTime)
                .last("LIMIT 5");
        List<Notice> notices = noticeMapper.selectList(noticeWrapper);

        List<DashboardVO.NoticeItem> noticeItems = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Notice notice : notices) {
            DashboardVO.NoticeItem item = new DashboardVO.NoticeItem();
            item.setId(notice.getId());
            item.setTitle(notice.getTitle());
            item.setType(notice.getType());
            if (notice.getPublishTime() != null) {
                item.setPublishTime(notice.getPublishTime().format(formatter));
            }
            noticeItems.add(item);
        }
        vo.setRecentNotices(noticeItems);

        // 各年级学生分布
        Map<String, Integer> gradeDistribution = new HashMap<>();
        String[] gradeNames = {"一年级", "二年级", "三年级", "四年级", "五年级", "六年级"};
        for (int i = 1; i <= 6; i++) {
            int finalI = i;
            LambdaQueryWrapper<Student> gradeWrapper = new LambdaQueryWrapper<>();
            gradeWrapper.eq(Student::getStatus, 1)
                    .inSql(Student::getClassId, "SELECT id FROM class WHERE grade = " + finalI);
            Long count = studentMapper.selectCount(gradeWrapper);
            gradeDistribution.put(gradeNames[i - 1], count.intValue());
        }
        vo.setGradeDistribution(gradeDistribution);

        return vo;
    }

    private void addScoreItem(List<DashboardVO.ScoreDistributionItem> list, String name, Object value) {
        DashboardVO.ScoreDistributionItem item = new DashboardVO.ScoreDistributionItem();
        item.setName(name);
        item.setValue(value != null ? ((Number) value).intValue() : 0);
        list.add(item);
    }
}
