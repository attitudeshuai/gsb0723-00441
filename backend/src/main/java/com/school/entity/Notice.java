package com.school.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.school.common.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 通知公告实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("notice")
@Schema(description = "通知公告")
public class Notice extends BaseEntity {

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "类型:1通知,2公告,3紧急")
    private Integer type;

    @Schema(description = "是否置顶")
    private Integer isTop;

    @Schema(description = "发布者ID")
    private Long publisherId;

    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    @Schema(description = "状态:1已发布,0草稿")
    private Integer status;

    @TableField(exist = false)
    @Schema(description = "发布者姓名")
    private String publisherName;
}
