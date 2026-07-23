package com.school.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 首页仪表盘响应VO
 */
@Data
@Schema(description = "首页仪表盘响应")
public class DashboardVO {

    @Schema(description = "学生总数")
    private Long studentCount;

    @Schema(description = "教师总数")
    private Long teacherCount;

    @Schema(description = "班级总数")
    private Long classCount;

    @Schema(description = "今日出勤率(%)")
    private BigDecimal todayAttendanceRate;

    @Schema(description = "学生数量趋势(%)")
    private BigDecimal studentTrend;

    @Schema(description = "教师数量趋势")
    private Integer teacherTrend;

    @Schema(description = "出勤率趋势(%)")
    private BigDecimal attendanceTrend;

    @Schema(description = "成绩分布")
    private List<ScoreDistributionItem> scoreDistribution;

    @Schema(description = "考勤趋势数据")
    private List<AttendanceTrendItem> attendanceTrendData;

    @Schema(description = "最近公告列表")
    private List<NoticeItem> recentNotices;

    @Schema(description = "各年级学生分布")
    private Map<String, Integer> gradeDistribution;

    @Data
    @Schema(description = "公告项")
    public static class NoticeItem {
        @Schema(description = "公告ID")
        private Long id;

        @Schema(description = "标题")
        private String title;

        @Schema(description = "类型")
        private Integer type;

        @Schema(description = "发布时间")
        private String publishTime;
    }

    @Data
    @Schema(description = "成绩分布项")
    public static class ScoreDistributionItem {
        @Schema(description = "分数段名称")
        private String name;

        @Schema(description = "人数")
        private Integer value;
    }

    @Data
    @Schema(description = "考勤趋势项")
    public static class AttendanceTrendItem {
        @Schema(description = "日期")
        private String date;

        @Schema(description = "出勤率")
        private BigDecimal rate;
    }
}
