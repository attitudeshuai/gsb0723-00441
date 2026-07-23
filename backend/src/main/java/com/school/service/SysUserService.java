package com.school.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.school.common.PageResult;
import com.school.dto.UserDTO;
import com.school.entity.SysUser;
import com.school.vo.UserVO;

/**
 * 用户服务接口
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 分页查询用户
     */
    PageResult<UserVO> listUsers(Integer page, Integer size, String username, String realName, Long roleId);

    /**
     * 新增用户
     */
    void addUser(UserDTO dto);

    /**
     * 更新用户
     */
    void updateUser(UserDTO dto);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 重置密码
     */
    void resetPassword(Long id);
}
