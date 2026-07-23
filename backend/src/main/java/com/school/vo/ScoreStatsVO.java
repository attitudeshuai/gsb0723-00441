package com.school.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 成绩统计响应VO
 */
@Data
@Schema(description = "成绩统计响应")
public class ScoreStatsVO {

    @Schema(description = "考试名称")
    private String examName;

    @Schema(description = "班级名称")
    private String className;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "学生人数")
    private Integer studentCount;

    @Schema(description = "平均分")
    private BigDecimal avgScore;

    @Schema(description = "最高分")
    private BigDecimal maxScore;

    @Schema(description = "最低分")
    private BigDecimal minScore;

    @Schema(description = "及格率(%)")
    private BigDecimal passRate;

    @Schema(description = "优秀率(%)")
    private BigDecimal excellentRate;

    @Schema(description = "分数分布")
    private ScoreDistribution distribution;

    @Schema(description = "各科统计")
    private List<SubjectStatItem> subjectStats;

    @Schema(description = "成绩排名Top10")
    private List<TopStudentItem> topStudents;

    @Data
    @Schema(description = "分数分布")
    public static class ScoreDistribution {
        @Schema(description = "优秀人数(90-100)")
        private Integer excellent;

        @Schema(description = "良好人数(80-89)")
        private Integer good;

        @Schema(description = "及格人数(60-79)")
        private Integer pass;

        @Schema(description = "不及格人数(<60)")
        private Integer fail;
    }

    @Data
    @Schema(description = "科目统计项")
    public static class SubjectStatItem {
        @Schema(description = "课程ID")
        private Long courseId;

        @Schema(description = "课程名称")
        private String courseName;

        @Schema(description = "平均分")
        private BigDecimal avgScore;

        @Schema(description = "最高分")
        private BigDecimal maxScore;

        @Schema(description = "最低分")
        private BigDecimal minScore;
    }

    @Data
    @Schema(description = "排名学生项")
    public static class TopStudentItem {
        @Schema(description = "学生ID")
        private Long studentId;

        @Schema(description = "学生姓名")
        private String studentName;

        @Schema(description = "班级名称")
        private String className;

        @Schema(description = "总分")
        private BigDecimal totalScore;

        @Schema(description = "平均分")
        private BigDecimal avgScore;
    }
}
