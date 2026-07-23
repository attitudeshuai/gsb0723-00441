package com.school.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 用户请求DTO
 */
@Data
@Schema(description = "用户请求")
public class UserDTO {

    @Schema(description = "用户ID")
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度需在4-20个字符之间")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_]*$", message = "用户名需字母开头，只能包含字母、数字和下划线")
    @Schema(description = "用户名", required = true)
    private String username;

    @Size(min = 6, max = 20, message = "密码长度需在6-20个字符之间")
    @Schema(description = "密码")
    private String password;

    @NotBlank(message = "真实姓名不能为空")
    @Size(max = 20, message = "姓名长度不能超过20个字符")
    @Schema(description = "真实姓名", required = true)
    private String realName;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入正确的手机号")
    @Schema(description = "手机号")
    private String phone;

    @NotNull(message = "角色不能为空")
    @Schema(description = "角色ID", required = true)
    private Long roleId;

    @Schema(description = "状态:1启用,0禁用")
    private Integer status;
}
