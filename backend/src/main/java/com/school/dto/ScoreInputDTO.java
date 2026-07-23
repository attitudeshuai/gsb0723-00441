package com.school.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 成绩录入请求DTO
 */
@Data
@Schema(description = "成绩录入请求")
public class ScoreInputDTO {

    @NotNull(message = "考试ID不能为空")
    @Schema(description = "考试ID", required = true)
    private Long examId;

    @NotNull(message = "课程ID不能为空")
    @Schema(description = "课程ID", required = true)
    private Long courseId;

    @NotNull(message = "成绩列表不能为空")
    @NotEmpty(message = "成绩列表不能为空")
    @Valid
    @Schema(description = "成绩列表", required = true)
    private List<ScoreItem> scores;

    @Data
    @Schema(description = "成绩项")
    public static class ScoreItem {
        @NotNull(message = "学生ID不能为空")
        @Schema(description = "学生ID", required = true)
        private Long studentId;

        @DecimalMin(value = "0", message = "分数不能小于0")
        @DecimalMax(value = "100", message = "分数不能超过100")
        @Schema(description = "分数")
        private BigDecimal score;
    }
}
