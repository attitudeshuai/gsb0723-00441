package com.school.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.school.common.ResultCode;
import com.school.dto.ClassDTO;
import com.school.entity.SchoolClass;
import com.school.exception.BusinessException;
import com.school.mapper.SchoolClassMapper;
import com.school.mapper.StudentMapper;
import com.school.service.SchoolClassService;
import com.school.vo.ClassVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 班级服务实现类
 */
@Service
@RequiredArgsConstructor
public class SchoolClassServiceImpl extends ServiceImpl<SchoolClassMapper, SchoolClass> implements SchoolClassService {

    private final StudentMapper studentMapper;

    @Override
    public List<ClassVO> listClasses(Integer grade) {
        return baseMapper.selectClassList(grade);
    }

    @Override
    public void addClass(ClassDTO dto) {
        // 检查班级是否已存在
        LambdaQueryWrapper<SchoolClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SchoolClass::getGrade, dto.getGrade())
                .eq(SchoolClass::getClassNo, dto.getClassNo());
        if (count(wrapper) > 0) {
            throw new BusinessException(ResultCode.CLASS_EXISTS);
        }

        SchoolClass clazz = new SchoolClass();
        BeanUtils.copyProperties(dto, clazz);
        clazz.setStudentCount(0);
        save(clazz);
    }

    @Override
    public void updateClass(ClassDTO dto) {
        SchoolClass clazz = getById(dto.getId());
        if (clazz == null) {
            throw new BusinessException(ResultCode.CLASS_NOT_FOUND);
        }

        // 检查班级是否已被其他记录使用
        LambdaQueryWrapper<SchoolClass> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SchoolClass::getGrade, dto.getGrade())
                .eq(SchoolClass::getClassNo, dto.getClassNo())
                .ne(SchoolClass::getId, dto.getId());
        if (count(wrapper) > 0) {
            throw new BusinessException(ResultCode.CLASS_EXISTS);
        }

        BeanUtils.copyProperties(dto, clazz, "studentCount");
        updateById(clazz);
    }

    @Override
    public void deleteClass(Long id) {
        SchoolClass clazz = getById(id);
        if (clazz == null) {
            throw new BusinessException(ResultCode.CLASS_NOT_FOUND);
        }

        // 检查班级下是否有学生
        int studentCount = studentMapper.countByClassId(id);
        if (studentCount > 0) {
            throw new BusinessException(ResultCode.CLASS_HAS_STUDENTS);
        }

        removeById(id);
    }

    @Override
    public void updateStudentCount(Long classId) {
        int count = studentMapper.countByClassId(classId);
        SchoolClass clazz = new SchoolClass();
        clazz.setId(classId);
        clazz.setStudentCount(count);
        updateById(clazz);
    }
}
