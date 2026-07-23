package com.school.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 考勤统计响应VO
 */
@Data
@Schema(description = "考勤统计响应")
public class AttendanceStatsVO {

    @Schema(description = "班级名称")
    private String className;

    @Schema(description = "统计天数")
    private Integer totalDays;

    @Schema(description = "学生人数")
    private Integer studentCount;

    @Schema(description = "出勤率(%)")
    private BigDecimal attendanceRate;

    @Schema(description = "学生考勤详情")
    private List<StudentAttendance> details;

    @Data
    @Schema(description = "学生考勤详情")
    public static class StudentAttendance {
        @Schema(description = "学生ID")
        private Long studentId;

        @Schema(description = "学生姓名")
        private String studentName;

        @Schema(description = "出勤次数")
        private Integer attendCount;

        @Schema(description = "迟到次数")
        private Integer lateCount;

        @Schema(description = "请假次数")
        private Integer leaveCount;

        @Schema(description = "旷课次数")
        private Integer absentCount;

        @Schema(description = "出勤率(%)")
        private BigDecimal rate;
    }
}
