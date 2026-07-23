package com.school.service.impl;

import cn.hutool.json.JSONUtil;
import com.school.common.ResultCode;
import com.school.dto.LoginDTO;
import com.school.entity.SysUser;
import com.school.exception.BusinessException;
import com.school.mapper.SysUserMapper;
import com.school.service.AuthService;
import com.school.util.JwtUtil;
import com.school.util.PasswordUtil;
import com.school.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 认证服务实现类
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final JwtUtil jwtUtil;
    private final PasswordUtil passwordUtil;

    @Override
    public LoginVO login(LoginDTO dto) {
        SysUser user = sysUserMapper.selectByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.LOGIN_FAILED);
        }

        if (!passwordUtil.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.LOGIN_FAILED);
        }

        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        sysUserMapper.updateById(user);

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRoleId());

        // 构建返回信息
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserInfo(buildUserInfo(user));

        return vo;
    }

    @Override
    public LoginVO.UserInfo getUserInfo(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        SysUser userWithRole = sysUserMapper.selectByUsername(user.getUsername());
        return buildUserInfo(userWithRole);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (!passwordUtil.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        user.setPassword(passwordUtil.encode(newPassword));
        sysUserMapper.updateById(user);
    }

    private LoginVO.UserInfo buildUserInfo(SysUser user) {
        LoginVO.UserInfo userInfo = new LoginVO.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setPhone(user.getPhone());
        userInfo.setRoleId(user.getRoleId());
        userInfo.setRoleName(user.getRoleName());

        if (user.getPermissions() != null) {
            List<String> permissions = JSONUtil.toList(user.getPermissions(), String.class);
            userInfo.setPermissions(permissions);
        }

        return userInfo;
    }
}
