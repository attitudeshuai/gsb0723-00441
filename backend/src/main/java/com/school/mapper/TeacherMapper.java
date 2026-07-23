package com.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.school.entity.Teacher;
import com.school.vo.TeacherVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 教师Mapper
 */
@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {

    /**
     * 分页查询教师（带班主任信息）
     */
    IPage<TeacherVO> selectTeacherPage(Page<TeacherVO> page, @Param("name") String name,
                                        @Param("subject") String subject);

    /**
     * 获取全部教师（下拉选择用）
     */
    @Select("SELECT id, name, teacher_no, subject, gender, status FROM teacher WHERE status = 1 ORDER BY name")
    List<TeacherVO> selectAllTeachers();

    /**
     * 获取最大工号
     */
    @Select("SELECT MAX(teacher_no) FROM teacher WHERE teacher_no LIKE #{prefix}")
    String selectMaxTeacherNo(@Param("prefix") String prefix);
}
