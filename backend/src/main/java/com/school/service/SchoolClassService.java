package com.school.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.school.dto.ClassDTO;
import com.school.entity.SchoolClass;
import com.school.vo.ClassVO;

import java.util.List;

/**
 * 班级服务接口
 */
public interface SchoolClassService extends IService<SchoolClass> {

    /**
     * 查询班级列表
     */
    List<ClassVO> listClasses(Integer grade);

    /**
     * 新增班级
     */
    void addClass(ClassDTO dto);

    /**
     * 更新班级
     */
    void updateClass(ClassDTO dto);

    /**
     * 删除班级
     */
    void deleteClass(Long id);

    /**
     * 更新学生人数
     */
    void updateStudentCount(Long classId);
}
