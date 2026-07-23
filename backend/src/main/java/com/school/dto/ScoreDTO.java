package com.school.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 单条成绩更新请求DTO
 */
@Data
public class ScoreDTO {

    /**
     * 成绩ID（更新时使用）
     */
    private Long id;

    /**
     * 考试ID
     */
    @NotNull(message = "考试ID不能为空")
    private Long examId;

    /**
     * 学生ID
     */
    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    /**
     * 分数
     */
    @NotNull(message = "分数不能为空")
    @DecimalMin(value = "0", message = "分数不能小于0")
    @DecimalMax(value = "150", message = "分数不能超过150")
    private BigDecimal score;

    /**
     * 备注
     */
    private String remark;
}
