package com.school.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.school.common.PageResult;
import com.school.dto.StudentDTO;
import com.school.entity.Student;
import com.school.vo.StudentVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 学生服务接口
 */
public interface StudentService extends IService<Student> {

    /**
     * 分页查询学生
     */
    PageResult<StudentVO> listStudents(Integer page, Integer size, Long classId, String name, Integer status);

    /**
     * 新增学生
     */
    String addStudent(StudentDTO dto);

    /**
     * 更新学生
     */
    void updateStudent(StudentDTO dto);

    /**
     * 删除学生
     */
    void deleteStudent(Long id);

    /**
     * 学生转班
     */
    void transferStudent(Long id, Long classId);

    /**
     * 获取学生详情
     */
    StudentVO getStudentDetail(Long id);

    /**
     * 获取班级学生列表
     */
    List<Student> getStudentsByClassId(Long classId);

    /**
     * 批量导入学生
     */
    Map<String, Object> importStudents(MultipartFile file) throws IOException;

    /**
     * 导出学生名单
     */
    void exportStudents(Long classId, HttpServletResponse response) throws IOException;
}
