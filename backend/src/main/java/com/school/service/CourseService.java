package com.school.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.school.entity.Course;

import java.util.List;

/**
 * 课程服务接口
 */
public interface CourseService extends IService<Course> {

    /**
     * 获取所有课程列表
     */
    List<Course> listCourses();
}
