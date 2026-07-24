package com.school.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.school.common.PageResult;
import com.school.common.ResultCode;
import com.school.dto.StudentDTO;
import com.school.entity.SchoolClass;
import com.school.entity.Student;
import com.school.exception.BusinessException;
import com.school.mapper.SchoolClassMapper;
import com.school.mapper.StudentMapper;
import com.school.service.SchoolClassService;
import com.school.service.StudentService;
import com.school.vo.StudentVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生服务实现类
 */
@Service
@RequiredArgsConstructor
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    private final SchoolClassMapper classMapper;
    private final SchoolClassService classService;

    @Override
    public PageResult<StudentVO> listStudents(Integer page, Integer size, Long classId, String name, Integer status) {
        Page<StudentVO> pageParam = new Page<>(page, size);
        var result = baseMapper.selectStudentPage(pageParam, classId, name, status);

        // 设置文本字段
        result.getRecords().forEach(this::setTextFields);

        return PageResult.of(result);
    }

    @Override
    @Transactional
    public String addStudent(StudentDTO dto) {
        // 验证班级
        SchoolClass clazz = classMapper.selectById(dto.getClassId());
        if (clazz == null) {
            throw new BusinessException(ResultCode.CLASS_NOT_FOUND);
        }

        Student student = new Student();
        BeanUtils.copyProperties(dto, student);

        // 生成学号
        String studentNo = generateStudentNo(clazz);
        student.setStudentNo(studentNo);
        student.setStatus(1);

        if (dto.getEnrollDate() == null) {
            student.setEnrollDate(LocalDate.now());
        }

        save(student);

        // 更新班级学生人数
        classService.updateStudentCount(dto.getClassId());

        return studentNo;
    }

    @Override
    @Transactional
    public void updateStudent(StudentDTO dto) {
        Student student = getById(dto.getId());
        if (student == null) {
            throw new BusinessException(ResultCode.STUDENT_NOT_FOUND);
        }

        Long oldClassId = student.getClassId();
        BeanUtils.copyProperties(dto, student, "studentNo");
        updateById(student);

        // 如果班级变更，更新两个班级的学生人数
        if (!oldClassId.equals(dto.getClassId())) {
            classService.updateStudentCount(oldClassId);
            classService.updateStudentCount(dto.getClassId());
        }
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        Student student = getById(id);
        if (student == null) {
            throw new BusinessException(ResultCode.STUDENT_NOT_FOUND);
        }

        removeById(id);
        classService.updateStudentCount(student.getClassId());
    }

    @Override
    @Transactional
    public void transferStudent(Long id, Long classId) {
        Student student = getById(id);
        if (student == null) {
            throw new BusinessException(ResultCode.STUDENT_NOT_FOUND);
        }

        SchoolClass clazz = classMapper.selectById(classId);
        if (clazz == null) {
            throw new BusinessException(ResultCode.CLASS_NOT_FOUND);
        }

        Long oldClassId = student.getClassId();
        student.setClassId(classId);
        updateById(student);

        // 更新两个班级的学生人数
        classService.updateStudentCount(oldClassId);
        classService.updateStudentCount(classId);
    }

    @Override
    public StudentVO getStudentDetail(Long id) {
        Student student = getById(id);
        if (student == null) {
            throw new BusinessException(ResultCode.STUDENT_NOT_FOUND);
        }

        StudentVO vo = new StudentVO();
        BeanUtils.copyProperties(student, vo);

        SchoolClass clazz = classMapper.selectById(student.getClassId());
        if (clazz != null) {
            vo.setClassName(clazz.getClassName());
        }

        setTextFields(vo);
        return vo;
    }

    @Override
    public List<Student> getStudentsByClassId(Long classId) {
        return baseMapper.selectByClassId(classId);
    }

    @Override
    public Map<String, Object> importStudents(MultipartFile file) throws IOException {
        // 简化实现，实际项目中需要使用EasyExcel解析
        Map<String, Object> result = new HashMap<>();
        result.put("successCount", 0);
        result.put("failCount", 0);
        result.put("failList", List.of());
        return result;
    }

    @Override
    public void exportStudents(Long classId, HttpServletResponse response) throws IOException {
        // 简化实现，实际项目中需要使用EasyExcel导出
    }

    private String generateStudentNo(SchoolClass clazz) {
        int year = LocalDate.now().getYear();
        String prefix = String.format("%d%02d", year, clazz.getGrade());

        String maxNo = baseMapper.selectMaxStudentNo(prefix + "%");
        int seq = 1;
        if (maxNo != null && maxNo.length() > prefix.length()) {
            seq = Integer.parseInt(maxNo.substring(prefix.length())) + 1;
        }

        return String.format("%s%04d", prefix, seq);
    }

    private void setTextFields(StudentVO vo) {
        vo.setGenderText(vo.getGender() == 1 ? "男" : "女");
        switch (vo.getStatus()) {
            case 1 -> vo.setStatusText("在读");
            case 2 -> vo.setStatusText("转出");
            case 3 -> vo.setStatusText("毕业");
        }
    }
}
