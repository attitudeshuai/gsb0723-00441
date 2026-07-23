package com.school.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.school.dto.ScoreInputDTO;
import com.school.entity.Course;
import com.school.entity.Exam;
import com.school.entity.SchoolClass;
import com.school.entity.Score;
import com.school.entity.Student;
import com.school.mapper.CourseMapper;
import com.school.mapper.ExamMapper;
import com.school.mapper.SchoolClassMapper;
import com.school.mapper.ScoreMapper;
import com.school.mapper.StudentMapper;
import com.school.service.ScoreService;
import com.school.vo.ScoreStatsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 成绩服务实现类
 */
@Service
@RequiredArgsConstructor
public class ScoreServiceImpl extends ServiceImpl<ScoreMapper, Score> implements ScoreService {

    private final ExamMapper examMapper;
    private final CourseMapper courseMapper;
    private final SchoolClassMapper classMapper;
    private final StudentMapper studentMapper;

    @Override
    public List<Score> listScores(Long examId, Long classId, Long courseId) {
        return baseMapper.selectScoreList(examId, classId, courseId);
    }

    @Override
    @Transactional
    public void batchInputScores(ScoreInputDTO dto) {
        for (ScoreInputDTO.ScoreItem item : dto.getScores()) {
            // 检查是否已存在记录
            LambdaQueryWrapper<Score> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Score::getStudentId, item.getStudentId())
                    .eq(Score::getExamId, dto.getExamId())
                    .eq(Score::getCourseId, dto.getCourseId());

            Score existing = getOne(wrapper);
            if (existing != null) {
                existing.setScore(item.getScore());
                updateById(existing);
            } else {
                Score score = new Score();
                score.setStudentId(item.getStudentId());
                score.setExamId(dto.getExamId());
                score.setCourseId(dto.getCourseId());
                score.setScore(item.getScore());
                save(score);
            }
        }
    }

    @Override
    public ScoreStatsVO getScoreStats(Long examId, Long classId, Long courseId) {
        ScoreStatsVO vo = new ScoreStatsVO();

        // 获取考试信息
        Exam exam = examMapper.selectById(examId);
        if (exam != null) {
            vo.setExamName(exam.getExamName());
        }

        // 获取班级信息
        if (classId != null) {
            SchoolClass clazz = classMapper.selectById(classId);
            if (clazz != null) {
                vo.setClassName(clazz.getClassName());
            }
        }

        // 如果指定了课程，返回单科统计
        if (courseId != null) {
            Course course = courseMapper.selectById(courseId);
            if (course != null) {
                vo.setCourseName(course.getCourseName());
            }
            Map<String, Object> stats = baseMapper.selectScoreStats(examId, classId, courseId);
            if (stats != null) {
                vo.setStudentCount(((Number) stats.get("studentCount")).intValue());
                vo.setAvgScore(toBigDecimal(stats.get("avgScore")));
                vo.setMaxScore(toBigDecimal(stats.get("maxScore")));
                vo.setMinScore(toBigDecimal(stats.get("minScore")));
                vo.setPassRate(toBigDecimal(stats.get("passRate")));
                vo.setExcellentRate(toBigDecimal(stats.get("excellentRate")));
            }
            vo.setDistribution(calculateDistribution(examId, classId, courseId));
            return vo;
        }

        // 否则返回全科统计（包含各科统计和排名）
        Map<String, Object> stats = baseMapper.selectScoreStats(examId, classId, null);
        if (stats != null) {
            vo.setStudentCount(((Number) stats.get("studentCount")).intValue());
            vo.setAvgScore(toBigDecimal(stats.get("avgScore")));
            vo.setMaxScore(toBigDecimal(stats.get("maxScore")));
            vo.setMinScore(toBigDecimal(stats.get("minScore")));
            vo.setPassRate(toBigDecimal(stats.get("passRate")));
            vo.setExcellentRate(toBigDecimal(stats.get("excellentRate")));
        }

        // 计算分数分布（所有科目）
        vo.setDistribution(calculateDistribution(examId, classId, null));

        // 获取各科统计
        vo.setSubjectStats(calculateSubjectStats(examId, classId));

        // 获取成绩排名 Top 10
        vo.setTopStudents(calculateTopStudents(examId, classId));

        return vo;
    }

    @Override
    public Map<String, Object> getScoreTrend(Long studentId, Long courseId) {
        List<Map<String, Object>> trends = baseMapper.selectScoreTrend(studentId, courseId);

        Map<String, Object> result = new HashMap<>();
        result.put("trends", trends);
        return result;
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value.toString()).setScale(2, RoundingMode.HALF_UP);
    }

    private ScoreStatsVO.ScoreDistribution calculateDistribution(Long examId, Long classId, Long courseId) {
        List<Score> scores = listScores(examId, classId, courseId);
        ScoreStatsVO.ScoreDistribution distribution = new ScoreStatsVO.ScoreDistribution();
        distribution.setExcellent(0);
        distribution.setGood(0);
        distribution.setPass(0);
        distribution.setFail(0);

        for (Score score : scores) {
            if (score.getScore() == null) continue;
            int s = score.getScore().intValue();
            if (s >= 90) {
                distribution.setGood(distribution.getGood() + 1);
            } else if (s >= 80) {
                distribution.setExcellent(distribution.getExcellent() + 1);
            } else if (s >= 60) {
                distribution.setPass(distribution.getPass() + 1);
            } else {
                distribution.setFail(distribution.getFail() + 1);
            }
        }

        return distribution;
    }

    private List<ScoreStatsVO.SubjectStatItem> calculateSubjectStats(Long examId, Long classId) {
        // 获取所有课程
        LambdaQueryWrapper<Course> courseWrapper = new LambdaQueryWrapper<>();
        List<Course> courses = courseMapper.selectList(courseWrapper);

        List<ScoreStatsVO.SubjectStatItem> subjectStats = new ArrayList<>();

        for (Course course : courses) {
            Map<String, Object> stats = baseMapper.selectScoreStats(examId, classId, course.getId());
            if (stats != null && ((Number) stats.get("studentCount")).intValue() > 0) {
                ScoreStatsVO.SubjectStatItem item = new ScoreStatsVO.SubjectStatItem();
                item.setCourseId(course.getId());
                item.setCourseName(course.getCourseName());
                item.setAvgScore(toBigDecimal(stats.get("avgScore")));
                item.setMaxScore(toBigDecimal(stats.get("maxScore")));
                item.setMinScore(toBigDecimal(stats.get("minScore")));
                subjectStats.add(item);
            }
        }

        // 按平均分降序排序
        subjectStats.sort(Comparator.comparing(
            ScoreStatsVO.SubjectStatItem::getAvgScore,
            Comparator.nullsLast(Comparator.reverseOrder())
        ));

        return subjectStats;
    }

    private List<ScoreStatsVO.TopStudentItem> calculateTopStudents(Long examId, Long classId) {
        // 获取该考试所有学生的成绩
        LambdaQueryWrapper<Score> scoreWrapper = new LambdaQueryWrapper<>();
        scoreWrapper.eq(Score::getExamId, examId);
        List<Score> scores = baseMapper.selectList(scoreWrapper);

        // 按学生分组计算总分和平均分
        Map<Long, List<Score>> studentScores = scores.stream()
            .collect(Collectors.groupingBy(Score::getStudentId));

        List<ScoreStatsVO.TopStudentItem> topStudents = new ArrayList<>();

        for (Map.Entry<Long, List<Score>> entry : studentScores.entrySet()) {
            Long studentId = entry.getKey();
            List<Score> studentScoreList = entry.getValue();

            // 过滤班级
            if (classId != null) {
                Student student = studentMapper.selectById(studentId);
                if (student == null || !classId.equals(student.getClassId())) {
                    continue;
                }
            }

            BigDecimal totalScore = studentScoreList.stream()
                .map(Score::getScore)
                .filter(score -> score != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal avgScore = totalScore.divide(
                BigDecimal.valueOf(studentScoreList.size()),
                2,
                RoundingMode.HALF_UP
            );

            Student student = studentMapper.selectById(studentId);
            if (student != null) {
                ScoreStatsVO.TopStudentItem item = new ScoreStatsVO.TopStudentItem();
                item.setStudentId(studentId);
                item.setStudentName(student.getName());

                if (student.getClassId() != null) {
                    SchoolClass clazz = classMapper.selectById(student.getClassId());
                    if (clazz != null) {
                        item.setClassName(clazz.getClassName());
                    }
                }

                item.setTotalScore(totalScore);
                item.setAvgScore(avgScore);
                topStudents.add(item);
            }
        }

        // 按总分降序排序，取前10
        topStudents.sort(Comparator.comparing(
            ScoreStatsVO.TopStudentItem::getTotalScore,
            Comparator.nullsLast(Comparator.reverseOrder())
        ));

        return topStudents.stream().limit(10).collect(Collectors.toList());
    }
}
