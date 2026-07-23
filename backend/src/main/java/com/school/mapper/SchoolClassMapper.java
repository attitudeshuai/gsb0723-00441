package com.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.school.entity.SchoolClass;
import com.school.vo.ClassVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 班级Mapper
 */
@Mapper
public interface SchoolClassMapper extends BaseMapper<SchoolClass> {

    /**
     * 查询班级列表（带班主任姓名）
     */
    List<ClassVO> selectClassList(@Param("grade") Integer grade);

    /**
     * 根据教师ID查询班级
     */
    SchoolClass selectByTeacherId(@Param("teacherId") Long teacherId);
}
