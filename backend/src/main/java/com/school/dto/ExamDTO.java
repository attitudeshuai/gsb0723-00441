package com.school.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 考试请求DTO
 */
@Data
@Schema(description = "考试请求")
public class ExamDTO {

    @Schema(description = "考试ID")
    private Long id;

    @NotBlank(message = "考试名称不能为空")
    @Schema(description = "考试名称", required = true)
    private String examName;

    @NotNull(message = "考试类型不能为空")
    @Schema(description = "考试类型:1期中,2期末,3单元测试,4月考", required = true)
    private Integer examType;

    @Schema(description = "年级")
    private Integer grade;

    @Schema(description = "学期(如2024-2025-1)")
    private String semester;

    @Schema(description = "考试日期")
    private LocalDate examDate;

    @Schema(description = "状态:1进行中,2已结束")
    private Integer status;
}
