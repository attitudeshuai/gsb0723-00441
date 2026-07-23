package com.school.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 考勤录入请求DTO
 */
@Data
@Schema(description = "考勤录入请求")
public class AttendanceInputDTO {

    @NotNull(message = "班级ID不能为空")
    @Schema(description = "班级ID", required = true)
    private Long classId;

    @NotNull(message = "考勤日期不能为空")
    @Schema(description = "考勤日期", required = true)
    private LocalDate attendDate;

    @NotNull(message = "考勤记录不能为空")
    @Schema(description = "考勤记录列表", required = true)
    private List<AttendanceRecord> records;

    @Data
    @Schema(description = "考勤记录项")
    public static class AttendanceRecord {
        @NotNull(message = "学生ID不能为空")
        @Schema(description = "学生ID", required = true)
        private Long studentId;

        @NotNull(message = "考勤状态不能为空")
        @Schema(description = "状态:1出勤,2迟到,3早退,4请假,5旷课", required = true)
        private Integer status;

        @Schema(description = "备注")
        private String remark;
    }
}
