package com.school.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.school.common.PageResult;
import com.school.common.ResultCode;
import com.school.dto.UserDTO;
import com.school.entity.SysUser;
import com.school.exception.BusinessException;
import com.school.mapper.SysUserMapper;
import com.school.service.SysUserService;
import com.school.util.PasswordUtil;
import com.school.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final PasswordUtil passwordUtil;

    @Override
    public PageResult<UserVO> listUsers(Integer page, Integer size, String username, String realName, Long roleId) {
        Page<UserVO> pageParam = new Page<>(page, size);
        var result = baseMapper.selectUserPage(pageParam, username, realName, roleId);
        return PageResult.of(result);
    }

    @Override
    public void addUser(UserDTO dto) {
        // 检查用户名是否存在
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, dto.getUsername());
        if (count(wrapper) > 0) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }

        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(passwordUtil.encode(dto.getPassword() != null ? dto.getPassword() : "123456"));
        user.setStatus(1);
        save(user);
    }

    @Override
    public void updateUser(UserDTO dto) {
        SysUser user = getById(dto.getId());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 检查用户名是否被其他用户使用
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, dto.getUsername())
                .ne(SysUser::getId, dto.getId());
        if (count(wrapper) > 0) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }

        BeanUtils.copyProperties(dto, user, "password");
        updateById(user);
    }

    @Override
    public void deleteUser(Long id) {
        if (id == 1L) {
            throw new BusinessException("不能删除超级管理员");
        }
        removeById(id);
    }

    @Override
    public void resetPassword(Long id) {
        SysUser user = getById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        user.setPassword(passwordUtil.encodeDefaultPassword());
        updateById(user);
    }
}
