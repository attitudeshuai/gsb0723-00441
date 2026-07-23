package com.school.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.school.dto.ExamDTO;
import com.school.entity.Exam;
import com.school.mapper.ExamMapper;
import com.school.service.ExamService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 考试服务实现类
 */
@Service
public class ExamServiceImpl extends ServiceImpl<ExamMapper, Exam> implements ExamService {

    @Override
    public List<Exam> listExams(Integer grade, String semester, Integer examType) {
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();
        if (grade != null) {
            wrapper.eq(Exam::getGrade, grade);
        }
        if (semester != null && !semester.isEmpty()) {
            wrapper.eq(Exam::getSemester, semester);
        }
        if (examType != null) {
            wrapper.eq(Exam::getExamType, examType);
        }
        wrapper.orderByDesc(Exam::getExamDate);
        return list(wrapper);
    }

    @Override
    public void addExam(ExamDTO dto) {
        Exam exam = new Exam();
        BeanUtils.copyProperties(dto, exam);
        exam.setStatus(1);
        save(exam);
    }

    @Override
    public void updateExam(ExamDTO dto) {
        Exam exam = new Exam();
        BeanUtils.copyProperties(dto, exam);
        updateById(exam);
    }

    @Override
    public void deleteExam(Long id) {
        removeById(id);
    }
}
