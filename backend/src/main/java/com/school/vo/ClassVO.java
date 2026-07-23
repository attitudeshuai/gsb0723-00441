package com.school.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 班级响应VO
 */
@Data
@Schema(description = "班级响应")
public class ClassVO {

    @Schema(description = "班级ID")
    private Long id;

    @Schema(description = "年级:1-6")
    private Integer grade;

    @Schema(description = "班号:1,2,3...")
    private Integer classNo;

    @Schema(description = "班级名称")
    private String className;

    @Schema(description = "班主任ID")
    private Long teacherId;

    @Schema(description = "班主任姓名")
    private String teacherName;

    @Schema(description = "学生人数")
    private Integer studentCount;
}
