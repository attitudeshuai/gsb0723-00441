package com.school.service;

import com.school.dto.LoginDTO;
import com.school.vo.LoginVO;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     */
    LoginVO login(LoginDTO dto);

    /**
     * 获取用户信息
     */
    LoginVO.UserInfo getUserInfo(Long userId);

    /**
     * 修改密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);
}
