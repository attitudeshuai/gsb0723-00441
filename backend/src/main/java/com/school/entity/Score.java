package com.school.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.school.common.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 成绩实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("score")
@Schema(description = "成绩")
public class Score extends BaseEntity {

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "考试ID")
    private Long examId;

    @Schema(description = "分数")
    private BigDecimal score;

    @TableField(exist = false)
    @Schema(description = "学生姓名")
    private String studentName;

    @TableField(exist = false)
    @Schema(description = "学号")
    private String studentNo;

    @TableField(exist = false)
    @Schema(description = "课程名称")
    private String courseName;

    @TableField(exist = false)
    @Schema(description = "考试名称")
    private String examName;
}
