package com.school.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 登录响应VO
 */
@Data
@Schema(description = "登录响应")
public class LoginVO {

    @Schema(description = "Token")
    private String token;

    @Schema(description = "用户信息")
    private UserInfo userInfo;

    @Data
    @Schema(description = "用户信息")
    public static class UserInfo {
        @Schema(description = "用户ID")
        private Long id;

        @Schema(description = "用户名")
        private String username;

        @Schema(description = "真实姓名")
        private String realName;

        @Schema(description = "手机号")
        private String phone;

        @Schema(description = "角色ID")
        private Long roleId;

        @Schema(description = "角色名称")
        private String roleName;

        @Schema(description = "权限列表")
        private List<String> permissions;
    }
}
