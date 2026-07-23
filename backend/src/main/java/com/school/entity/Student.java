package com.school.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.school.common.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 学生实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("student")
@Schema(description = "学生")
public class Student extends BaseEntity {

    @Schema(description = "学号")
    private String studentNo;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "性别:1男,2女")
    private Integer gender;

    @Schema(description = "出生日期")
    private LocalDate birthDate;

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "家长姓名")
    private String parentName;

    @Schema(description = "家长电话")
    private String parentPhone;

    @Schema(description = "家庭住址")
    private String address;

    @Schema(description = "状态:1在读,2转出,3毕业")
    private Integer status;

    @Schema(description = "入学日期")
    private LocalDate enrollDate;

    @TableField(exist = false)
    @Schema(description = "班级名称")
    private String className;
}
