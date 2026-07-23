package com.school.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 考试实体
 */
@Data
@TableName("exam")
@Schema(description = "考试")
public class Exam implements Serializable {

    @Schema(description = "主键ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "考试名称")
    private String examName;

    @Schema(description = "考试类型:1期中,2期末,3单元测试,4月考")
    private Integer examType;

    @Schema(description = "年级")
    private Integer grade;

    @Schema(description = "学期(如2024-2025-1)")
    private String semester;

    @Schema(description = "考试日期")
    private LocalDate examDate;

    @Schema(description = "状态:1进行中,2已结束")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "是否删除")
    @TableLogic
    private Integer deleted;
}
