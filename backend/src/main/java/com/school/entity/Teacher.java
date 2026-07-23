package com.school.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.school.common.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 教师实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("teacher")
@Schema(description = "教师")
public class Teacher extends BaseEntity {

    @Schema(description = "工号")
    private String teacherNo;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "性别:1男,2女")
    private Integer gender;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "任教科目")
    private String subject;

    @Schema(description = "状态:1在职,0离职")
    private Integer status;

    @Schema(description = "关联用户ID")
    private Long userId;

    @TableField(exist = false)
    @Schema(description = "是否班主任")
    private Boolean isHeadTeacher;

    @TableField(exist = false)
    @Schema(description = "所带班级名称")
    private String headTeacherClass;
}
