package com.school.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 学生响应VO
 */
@Data
@Schema(description = "学生响应")
public class StudentVO {

    @Schema(description = "学生ID")
    private Long id;

    @Schema(description = "学号")
    private String studentNo;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "性别:1男,2女")
    private Integer gender;

    @Schema(description = "性别文本")
    private String genderText;

    @Schema(description = "出生日期")
    private LocalDate birthDate;

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "班级名称")
    private String className;

    @Schema(description = "家长姓名")
    private String parentName;

    @Schema(description = "家长电话")
    private String parentPhone;

    @Schema(description = "家庭住址")
    private String address;

    @Schema(description = "状态:1在读,2转出,3毕业")
    private Integer status;

    @Schema(description = "状态文本")
    private String statusText;

    @Schema(description = "入学日期")
    private LocalDate enrollDate;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
