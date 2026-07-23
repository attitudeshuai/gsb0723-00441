package com.school.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.school.common.PageResult;
import com.school.dto.TeacherDTO;
import com.school.entity.Teacher;
import com.school.vo.TeacherVO;

import java.util.List;

/**
 * 教师服务接口
 */
public interface TeacherService extends IService<Teacher> {

    /**
     * 分页查询教师
     */
    PageResult<TeacherVO> listTeachers(Integer page, Integer size, String name, String subject);

    /**
     * 获取全部教师（下拉选择用）
     */
    List<TeacherVO> listAllTeachers();

    /**
     * 新增教师
     */
    String addTeacher(TeacherDTO dto);

    /**
     * 更新教师
     */
    void updateTeacher(TeacherDTO dto);

    /**
     * 删除教师
     */
    void deleteTeacher(Long id);
}
