package com.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.school.entity.Student;
import com.school.vo.StudentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 学生Mapper
 */
@Mapper
public interface StudentMapper extends BaseMapper<Student> {

    /**
     * 分页查询学生（带班级名称）
     */
    IPage<StudentVO> selectStudentPage(Page<StudentVO> page, @Param("classId") Long classId,
                                        @Param("name") String name, @Param("status") Integer status);

    /**
     * 查询班级学生列表
     */
    @Select("SELECT s.*, c.class_name FROM student s " +
            "LEFT JOIN class c ON s.class_id = c.id " +
            "WHERE s.class_id = #{classId} AND s.deleted = 0 ORDER BY s.student_no")
    List<Student> selectByClassId(@Param("classId") Long classId);

    /**
     * 统计班级学生数量
     */
    @Select("SELECT COUNT(*) FROM student WHERE class_id = #{classId} AND deleted = 0 AND status = 1")
    int countByClassId(@Param("classId") Long classId);

    /**
     * 获取最大学号
     */
    @Select("SELECT MAX(student_no) FROM student WHERE student_no LIKE #{prefix}")
    String selectMaxStudentNo(@Param("prefix") String prefix);
}
