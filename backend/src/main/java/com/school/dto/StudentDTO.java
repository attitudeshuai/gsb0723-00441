package com.school.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * 学生请求DTO
 */
@Data
@Schema(description = "学生请求")
public class StudentDTO {

    @Schema(description = "学生ID")
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

    @Past(message = "出生日期必须是过去的日期")
    @Schema(description = "出生日期")
    private LocalDate birthDate;

    @NotNull(message = "班级不能为空")
    @Schema(description = "班级ID", required = true)
    private Long classId;

    @Size(max = 20, message = "家长姓名长度不能超过20个字符")
    @Schema(description = "家长姓名")
    private String parentName;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入正确的家长电话")
    @Schema(description = "家长电话")
    private String parentPhone;

    @Size(max = 200, message = "家庭住址长度不能超过200个字符")
    @Schema(description = "家庭住址")
    private String address;

    @Min(value = 1, message = "状态值无效")
    @Max(value = 3, message = "状态值无效")
    @Schema(description = "状态:1在读,2转出,3毕业")
    private Integer status;

    @PastOrPresent(message = "入学日期不能是未来日期")
    @Schema(description = "入学日期")
    private LocalDate enrollDate;
}
