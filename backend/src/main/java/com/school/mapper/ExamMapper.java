package com.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.school.entity.Exam;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考试Mapper
 */
@Mapper
public interface ExamMapper extends BaseMapper<Exam> {
}
