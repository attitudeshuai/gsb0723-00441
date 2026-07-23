package com.school.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 考勤详情响应VO
 */
@Data
public class AttendanceVO {

    /**
     * 考勤ID
     */
    private Long id;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 学号
     */
    private String studentNo;

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 年级
     */
    private Integer grade;

    /**
     * 考勤日期
     */
    private LocalDate attendDate;

    /**
     * 考勤状态：1-出勤 2-迟到 3-早退 4-请假 5-缺勤
     */
    private Integer status;

    /**
     * 考勤状态描述
     */
    private String statusText;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 获取状态描述
     */
    public String getStatusText() {
        if (status == null) {
            return "";
        }
        return switch (status) {
            case 1 -> "出勤";
            case 2 -> "迟到";
            case 3 -> "早退";
            case 4 -> "请假";
            case 5 -> "缺勤";
            default -> "未知";
        };
    }
}
