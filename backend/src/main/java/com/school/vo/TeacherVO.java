package com.school.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 教师响应VO
 */
@Data
@Schema(description = "教师响应")
public class TeacherVO {

    @Schema(description = "教师ID")
    private Long id;

    @Schema(description = "工号")
    private String teacherNo;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "性别:1男,2女")
    private Integer gender;

    @Schema(description = "性别文本")
    private String genderText;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "任教科目")
    private String subject;

    @Schema(description = "状态:1在职,0离职")
    private Integer status;

    @Schema(description = "状态文本")
    private String statusText;

    @Schema(description = "是否班主任")
    private Boolean isHeadTeacher;

    @Schema(description = "所带班级名称")
    private String headTeacherClass;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
