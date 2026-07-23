package com.school.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 班级请求DTO
 */
@Data
@Schema(description = "班级请求")
public class ClassDTO {

    @Schema(description = "班级ID")
    private Long id;

    @NotNull(message = "年级不能为空")
    @Min(value = 1, message = "年级需在1-6之间")
    @Max(value = 6, message = "年级需在1-6之间")
    @Schema(description = "年级:1-6", required = true)
    private Integer grade;

    @NotNull(message = "班号不能为空")
    @Min(value = 1, message = "班号需大于0")
    @Max(value = 20, message = "班号不能超过20")
    @Schema(description = "班号:1,2,3...", required = true)
    private Integer classNo;

    @NotBlank(message = "班级名称不能为空")
    @Size(max = 50, message = "班级名称长度不能超过50个字符")
    @Schema(description = "班级名称", required = true)
    private String className;

    @Schema(description = "班主任ID")
    private Long teacherId;
}
