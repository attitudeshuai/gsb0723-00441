package com.school.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.school.entity.SysRole;

import java.util.List;

/**
 * 角色服务接口
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 获取所有角色列表
     */
    List<SysRole> listRoles();
}
