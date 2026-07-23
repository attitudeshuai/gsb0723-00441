package com.school.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 通知公告请求DTO
 */
@Data
@Schema(description = "通知公告请求")
public class NoticeDTO {

    @Schema(description = "公告ID")
    private Long id;

    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100个字符")
    @Schema(description = "标题", required = true)
    private String title;

    @Size(max = 10000, message = "内容长度不能超过10000个字符")
    @Schema(description = "内容")
    private String content;

    @Min(value = 1, message = "类型值无效")
    @Max(value = 3, message = "类型值无效")
    @Schema(description = "类型:1通知,2公告,3紧急")
    private Integer type;

    @Schema(description = "是否置顶")
    private Boolean isTop;
}
