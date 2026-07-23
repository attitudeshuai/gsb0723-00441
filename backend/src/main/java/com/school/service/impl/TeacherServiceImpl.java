package com.school.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.school.common.PageResult;
import com.school.common.ResultCode;
import com.school.dto.TeacherDTO;
import com.school.entity.Teacher;
import com.school.exception.BusinessException;
import com.school.mapper.TeacherMapper;
import com.school.service.TeacherService;
import com.school.vo.TeacherVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * 教师服务实现类
 */
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    @Override
    public PageResult<TeacherVO> listTeachers(Integer page, Integer size, String name, String subject) {
        Page<TeacherVO> pageParam = new Page<>(page, size);
        var result = baseMapper.selectTeacherPage(pageParam, name, subject);

        // 设置文本字段
        result.getRecords().forEach(vo -> {
            vo.setGenderText(vo.getGender() == 1 ? "男" : "女");
            vo.setStatusText(vo.getStatus() == 1 ? "在职" : "离职");
        });

        return PageResult.of(result);
    }

    @Override
    public List<TeacherVO> listAllTeachers() {
        List<TeacherVO> teachers = baseMapper.selectAllTeachers();
        teachers.forEach(vo -> {
            vo.setGenderText(vo.getGender() == 1 ? "男" : "女");
            vo.setStatusText(vo.getStatus() == 1 ? "在职" : "离职");
        });
        return teachers;
    }

    @Override
    public String addTeacher(TeacherDTO dto) {
        Teacher teacher = new Teacher();
        BeanUtils.copyProperties(dto, teacher);

        // 生成工号
        String teacherNo = generateTeacherNo();
        teacher.setTeacherNo(teacherNo);
        teacher.setStatus(1);

        save(teacher);
        return teacherNo;
    }

    @Override
    public void updateTeacher(TeacherDTO dto) {
        Teacher teacher = getById(dto.getId());
        if (teacher == null) {
            throw new BusinessException(ResultCode.TEACHER_NOT_FOUND);
        }

        BeanUtils.copyProperties(dto, teacher, "teacherNo");
        updateById(teacher);
    }

    @Override
    public void deleteTeacher(Long id) {
        Teacher teacher = getById(id);
        if (teacher == null) {
            throw new BusinessException(ResultCode.TEACHER_NOT_FOUND);
        }
        removeById(id);
    }

    private String generateTeacherNo() {
        int year = LocalDate.now().getYear();
        String prefix = "T" + year;

        String maxNo = baseMapper.selectMaxTeacherNo(prefix + "%");
        int seq = 1;
        if (maxNo != null && maxNo.length() > prefix.length()) {
            seq = Integer.parseInt(maxNo.substring(prefix.length())) + 1;
        }

        return String.format("%s%04d", prefix, seq);
    }
}
