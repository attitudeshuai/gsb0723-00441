package com.school.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.school.dto.ExamDTO;
import com.school.entity.Exam;

import java.util.List;

/**
 * 考试服务接口
 */
public interface ExamService extends IService<Exam> {

    /**
     * 查询考试列表
     */
    List<Exam> listExams(Integer grade, String semester, Integer examType);

    /**
     * 新增考试
     */
    void addExam(ExamDTO dto);

    /**
     * 更新考试
     */
    void updateExam(ExamDTO dto);

    /**
     * 删除考试
     */
    void deleteExam(Long id);
}
