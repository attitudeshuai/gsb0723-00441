package com.school.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 单条考勤更新请求DTO
 */
@Data
public class AttendanceDTO {

    /**
     * 考勤ID（更新时使用）
     */
    private Long id;

    /**
     * 学生ID
     */
    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    /**
     * 班级ID
     */
    @NotNull(message = "班级ID不能为空")
    private Long classId;

    /**
     * 考勤日期
     */
    @NotNull(message = "考勤日期不能为空")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate attendDate;

    /**
     * 考勤状态：1-出勤 2-迟到 3-早退 4-请假 5-缺勤
     */
    @NotNull(message = "考勤状态不能为空")
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
