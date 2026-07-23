package com.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.school.entity.SysUser;
import com.school.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户Mapper
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 分页查询用户（带角色名称）
     */
    IPage<UserVO> selectUserPage(Page<UserVO> page, @Param("username") String username,
                                  @Param("realName") String realName, @Param("roleId") Long roleId);

    /**
     * 根据用户名查询用户（带角色信息）
     */
    @Select("SELECT u.*, r.role_name, r.permissions FROM sys_user u " +
            "LEFT JOIN sys_role r ON u.role_id = r.id " +
            "WHERE u.username = #{username} AND u.deleted = 0")
    SysUser selectByUsername(@Param("username") String username);
}
