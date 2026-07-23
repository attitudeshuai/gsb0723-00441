package com.school.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 教师请求DTO
 */
@Data
@Schema(description = "教师请求")
public class TeacherDTO {

    @Schema(description = "教师ID")
    private Long id;

    @NotBlank(message = "姓名不能为空")
    @Size(max = 20, message = "姓名长度不能超过20个字符")
    @Schema(description = "姓名", required = true)
    private String name;

    @NotNull(message = "性别不能为空")
    @Min(value = 1, message = "性别值无效")
    @Max(value = 2, message = "性别值无效")
    @Schema(description = "性别:1男,2女", required = true)
    private Integer gender;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入正确的手机号")
    @Schema(description = "手机号")
    private String phone;

    @Size(max = 50, message = "任教科目长度不能超过50个字符")
    @Schema(description = "任教科目")
    private String subject;

    @Min(value = 0, message = "状态值无效")
    @Max(value = 1, message = "状态值无效")
    @Schema(description = "状态:1在职,0离职")
    private Integer status;
}
