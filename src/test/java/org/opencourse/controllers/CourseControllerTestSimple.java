package org.opencourse.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.opencourse.configs.TestSecurityConfig;
import org.opencourse.dto.request.CourseCreationDto;
import org.opencourse.dto.request.CourseUpdateDto;
import org.opencourse.models.Course;
import org.opencourse.models.Department;
import org.opencourse.models.User;
import org.opencourse.services.CourseManager;
import org.opencourse.utils.security.SecurityUtils;
import org.opencourse.utils.typeinfo.CourseType;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link CourseController}.
 * 
 * @author GitHub Copilot
 */
@WebMvcTest(CourseController.class)
@Import(TestSecurityConfig.class)
class CourseControllerTestSimple {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CourseManager courseManager;

    private User testUser;
    private Department testDepartment;
    private Course testCourse;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User(
                "testUser",
                "test@example.com",
                "hashedPassword",
                User.UserRole.ADMIN);

        // Create test department
        testDepartment = new Department("Computer Science");
        testDepartment = spy(testDepartment);
        when(testDepartment.getId()).thenReturn((byte) 1);

        // Create test course
        testCourse = new Course(
                "Data Structures and Algorithms",
                "CS201",
                testDepartment,
                CourseType.MAJOR_REQUIRED,
                new BigDecimal("3.0"));
        testCourse = spy(testCourse);
        when(testCourse.getId()).thenReturn((short) 1);
    }

    @Test
    @DisplayName("Should successfully get all courses")
    void getAllCourses_ShouldReturnSuccess() throws Exception {
        // Given
        List<Course> courses = Arrays.asList(testCourse);
        when(courseManager.getCourses()).thenReturn(courses);

        // When & Then
        mockMvc.perform(get("/course"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取课程列表成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Data Structures and Algorithms"));

        verify(courseManager).getCourses();
    }

    @Test
    @DisplayName("Should return empty list when no courses exist")
    void getAllCourses_WithNoCourses_ShouldReturnEmptyList() throws Exception {
        // Given
        when(courseManager.getCourses()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/course"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取课程列表成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Should successfully get course by ID")
    void getCourseById_WithValidId_ShouldReturnSuccess() throws Exception {
        // Given
        Short courseId = 1;
        when(courseManager.getCourseById(courseId)).thenReturn(testCourse);

        // When & Then
        mockMvc.perform(get("/course/{id}", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取课程信息成功"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Data Structures and Algorithms"));

        verify(courseManager).getCourseById(courseId);
    }

    @Test
    @DisplayName("Should return 404 when course not found by ID")
    void getCourseById_WithNonExistingId_ShouldReturn404() throws Exception {
        // Given
        Short courseId = 999;
        when(courseManager.getCourseById(courseId)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/course/{id}", courseId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("课程不存在"));

        verify(courseManager).getCourseById(courseId);
    }

    @Test
    @DisplayName("Should successfully search courses with keyword")
    void searchCourses_WithKeyword_ShouldReturnSuccess() throws Exception {
        // Given
        String keyword = "Data";
        List<Course> courses = Arrays.asList(testCourse);
        when(courseManager.getCourses(keyword)).thenReturn(courses);

        // When & Then
        mockMvc.perform(get("/course/search").param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("搜索课程成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("Data Structures and Algorithms"));

        verify(courseManager).getCourses(keyword);
    }

    @Test
    @DisplayName("Should successfully get courses by department")
    void getCoursesByDepartment_WithValidDepartmentId_ShouldReturnSuccess() throws Exception {
        // Given
        Byte departmentId = 1;
        List<Course> courses = Arrays.asList(testCourse);
        when(courseManager.getCoursesByDepartment(departmentId)).thenReturn(courses);

        // When & Then
        mockMvc.perform(get("/course/department/{departmentId}", departmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取部门课程列表成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].department.id").value(1));

        verify(courseManager).getCoursesByDepartment(departmentId);
    }
}
