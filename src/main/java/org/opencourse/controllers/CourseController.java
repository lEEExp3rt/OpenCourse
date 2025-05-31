package org.opencourse.controllers;

import org.opencourse.dto.request.CourseCreationDto;

public class CourseController {

    /**
     * 创建课程
     * 
     * @param courseCreationDto 课程创建数据传输对象
     * @return 创建的课程信息
     */
    public String createCourse(CourseCreationDto courseCreationDto) {
        // 这里应该调用服务层来处理课程创建逻辑
        // 例如：courseService.createCourse(courseCreationDto);
        return "Course created successfully";
    }
    /**
     * 获取课程信息
     * 
     * @param courseId 课程ID
     * @return 课程信息
     */
}