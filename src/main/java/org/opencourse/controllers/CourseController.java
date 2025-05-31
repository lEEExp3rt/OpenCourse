package org.opencourse.controllers;

import jakarta.validation.Valid;
import org.opencourse.dto.request.CourseCreationDto;
import org.opencourse.dto.request.CourseUpdateDto;
import org.opencourse.dto.response.ApiResponse;
import org.opencourse.models.Course;
import org.opencourse.models.User;
import org.opencourse.services.CourseManager;
import org.opencourse.utils.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 课程管理控制器
 * 
 * @author GitHub Copilot
 */
@RestController
@RequestMapping("/course")
public class CourseController {

    private final CourseManager courseManager;

    @Autowired
    public CourseController(CourseManager courseManager) {
        this.courseManager = courseManager;
    }

    /**
     * 新增课程
     * 
     * @param creationDto 课程创建信息
     * @return 创建结果
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> addCourse(
            @Valid @RequestBody CourseCreationDto creationDto) {
        try {
            User user = SecurityUtils.getCurrentUser();
            Course course = courseManager.addCourse(creationDto, user);

            if (course == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("课程代码已存在"));
            }

            Map<String, Object> data = createCourseData(course);
            return ResponseEntity.ok(ApiResponse.success("课程创建成功", data));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("服务器内部错误"));
        }
    }

    /**
     * 更新课程信息
     * 
     * @param updateDto 课程更新信息
     * @return 更新结果
     */
    @PutMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateCourse(
            @Valid @RequestBody CourseUpdateDto updateDto) {
        try {
            User user = SecurityUtils.getCurrentUser();
            Course course = courseManager.updateCourse(updateDto, user);

            if (course == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("课程不存在或课程代码已被使用"));
            }

            Map<String, Object> data = createCourseData(course);
            return ResponseEntity.ok(ApiResponse.success("课程更新成功", data));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("更新课程失败"));
        }
    }

    /**
     * 删除课程
     * 
     * @param id 课程ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Short id) {
        try {
            User user = SecurityUtils.getCurrentUser();
            boolean success = courseManager.deleteCourse(id, user);

            if (success) {
                return ResponseEntity.ok(ApiResponse.success("课程删除成功"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("课程不存在"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("删除课程失败"));
        }
    }

    /**
     * 获取所有课程
     * 
     * @return 课程列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllCourses() {
        try {
            List<Course> courses = courseManager.getCourses();
            List<Map<String, Object>> data = courses.stream()
                    .map(this::createCourseData)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("获取课程列表成功", data));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取课程列表失败"));
        }
    }

    /**
     * 通过关键字查询课程
     * 
     * @param keyword 搜索关键字（课程名称或代码，可选）
     * @return 匹配的课程列表
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> searchCourses(
            @RequestParam(required = false) String keyword) {
        try {
            List<Course> courses = courseManager.getCourses(keyword);
            List<Map<String, Object>> data = courses.stream()
                    .map(this::createCourseData)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("搜索课程成功", data));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("搜索课程失败"));
        }
    }

    /**
     * 根据ID查询课程
     * 
     * @param id 课程ID
     * @return 课程信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCourseById(@PathVariable Short id) {
        try {
            Course course = courseManager.getCourseById(id);

            if (course == null) {
                return ResponseEntity.status(404).body(ApiResponse.error("课程不存在"));
            }

            Map<String, Object> data = createCourseData(course);
            return ResponseEntity.ok(ApiResponse.success("获取课程信息成功", data));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取课程信息失败"));
        }
    }

    /**
     * 根据部门查询课程
     * 
     * @param departmentId 部门ID
     * @return 课程列表
     */
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getCoursesByDepartment(
            @PathVariable Byte departmentId) {
        try {
            List<Course> courses = courseManager.getCoursesByDepartment(departmentId);
            List<Map<String, Object>> data = courses.stream()
                    .map(this::createCourseData)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("获取部门课程列表成功", data));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取部门课程列表失败"));
        }
    }

    /**
     * 根据课程类型查询课程
     * 
     * @param courseTypeId 课程类型ID
     * @return 课程列表
     */
    @GetMapping("/type/{courseTypeId}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getCoursesByType(
            @PathVariable byte courseTypeId) {
        try {
            List<Course> courses = courseManager.getCoursesByType(courseTypeId);
            List<Map<String, Object>> data = courses.stream()
                    .map(this::createCourseData)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("获取课程类型列表成功", data));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取课程类型列表失败"));
        }
    }

    /**
     * 根据部门和课程类型查询课程
     * 
     * @param departmentId 部门ID
     * @param courseTypeId 课程类型ID
     * @return 课程列表
     */
    @GetMapping("/department/{departmentId}/type/{courseTypeId}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getCoursesByDepartmentAndType(
            @PathVariable Byte departmentId, @PathVariable byte courseTypeId) {
        try {
            List<Course> courses = courseManager.getCoursesByDepartmentAndType(departmentId, courseTypeId);
            List<Map<String, Object>> data = courses.stream()
                    .map(this::createCourseData)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("获取课程列表成功", data));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取课程列表失败"));
        }
    }

    /**
     * 创建课程数据映射
     * 
     * @param course 课程实体
     * @return 课程数据映射
     */
    private Map<String, Object> createCourseData(Course course) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", course.getId());
        data.put("name", course.getName());
        data.put("code", course.getCode());
        data.put("credits", course.getCredits());
        
        // 添加部门信息
        if (course.getDepartment() != null) {
            Map<String, Object> departmentData = new HashMap<>();
            departmentData.put("id", course.getDepartment().getId());
            departmentData.put("name", course.getDepartment().getName());
            data.put("department", departmentData);
        }
        
        // 添加课程类型信息
        if (course.getCourseType() != null) {
            Map<String, Object> courseTypeData = new HashMap<>();
            courseTypeData.put("id", course.getCourseType().getId());
            courseTypeData.put("name", course.getCourseType().getName());
            courseTypeData.put("description", course.getCourseType().getDescription());
            data.put("courseType", courseTypeData);
        }
        
        return data;
    }
}