package com.school.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 课程实体
 */
@Data
@TableName("course")
@Schema(description = "课程")
public class Course implements Serializable {

    @Schema(description = "主键ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "课程代码")
    private String courseCode;

    @Schema(description = "适用年级")
    private Integer grade;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "是否删除")
    @TableLogic
    private Integer deleted;
}
