package com.school.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.school.common.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 班级实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("class")
@Schema(description = "班级")
public class SchoolClass extends BaseEntity {

    @Schema(description = "年级:1-6")
    private Integer grade;

    @Schema(description = "班号:1,2,3...")
    private Integer classNo;

    @Schema(description = "班级名称")
    private String className;

    @Schema(description = "班主任ID")
    private Long teacherId;

    @Schema(description = "学生人数")
    private Integer studentCount;

    @TableField(exist = false)
    @Schema(description = "班主任姓名")
    private String teacherName;
}
