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
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link CourseController}.
 * 
 * @author GitHub Copilot
 */
@WebMvcTest(CourseController.class)
@Import(TestSecurityConfig.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CourseManager courseManager;

    private User testUser;
    private Department testDepartment;
    private Course testCourse;
    private CourseCreationDto courseCreationDto;
    private CourseUpdateDto courseUpdateDto;

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

        // Create DTOs
        courseCreationDto = new CourseCreationDto(
                "Data Structures and Algorithms",
                "CS201",
                (byte) 1,
                (byte) 13, // MAJOR_REQUIRED
                new BigDecimal("3.0"));

        courseUpdateDto = new CourseUpdateDto(
                (short) 1,
                "Advanced Data Structures",
                "CS201",
                (byte) 1,
                (byte) 13,
                new BigDecimal("4.0"));
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
    @DisplayName("Should return bad request when course code already exists")
    void addCourse_WithExistingCode_ShouldReturnBadRequest() throws Exception {
        // Given
        try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
            when(courseManager.addCourse(any(CourseCreationDto.class), eq(testUser))).thenReturn(null);

            // When & Then
            mockMvc.perform(post("/course")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(courseCreationDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("课程代码已存在"));

            verify(courseManager).addCourse(any(CourseCreationDto.class), eq(testUser));
        }
    }

    @Test
    @DisplayName("Should return bad request when course name is blank")
    void addCourse_WithBlankName_ShouldReturnBadRequest() throws Exception {
        // Given
        CourseCreationDto invalidDto = new CourseCreationDto(
                "", // blank name
                "CS201",
                (byte) 1,
                (byte) 13,
                new BigDecimal("3.0"));

        // When & Then
        try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);

            mockMvc.perform(post("/course")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidDto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException from service")
    void addCourse_WithInvalidDepartment_ShouldReturnBadRequest() throws Exception {
        // Given
        try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
            when(courseManager.addCourse(any(CourseCreationDto.class), eq(testUser)))
                    .thenThrow(new IllegalArgumentException("Department not found."));

            // When & Then
            mockMvc.perform(post("/course")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(courseCreationDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("Department not found."));
        }
    }

    @Test
    @DisplayName("Should successfully update course with valid request")
    void updateCourse_WithValidRequest_ShouldReturnSuccess() throws Exception {
        // Given
        Course updatedCourse = spy(new Course(
                "Advanced Data Structures",
                "CS201",
                testDepartment,
                CourseType.MAJOR_REQUIRED,
                new BigDecimal("4.0")));
        when(updatedCourse.getId()).thenReturn((short) 1);

        try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
            when(courseManager.updateCourse(any(CourseUpdateDto.class), eq(testUser))).thenReturn(updatedCourse);

            // When & Then
            mockMvc.perform(put("/course")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(courseUpdateDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("课程更新成功"))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.name").value("Advanced Data Structures"))
                    .andExpect(jsonPath("$.data.credits").value(4.0));

            verify(courseManager).updateCourse(any(CourseUpdateDto.class), eq(testUser));
        }
    }

    @Test
    @DisplayName("Should return bad request when updating non-existing course")
    void updateCourse_WithNonExistingCourse_ShouldReturnBadRequest() throws Exception {
        // Given
        try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
            when(courseManager.updateCourse(any(CourseUpdateDto.class), eq(testUser))).thenReturn(null);

            // When & Then
            mockMvc.perform(put("/course")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(courseUpdateDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("课程不存在或课程代码已被使用"));
        }
    }

    @Test
    @DisplayName("Should successfully delete course")
    void deleteCourse_WithValidId_ShouldReturnSuccess() throws Exception {
        // Given
        Short courseId = 1;
        try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
            when(courseManager.deleteCourse(courseId, testUser)).thenReturn(true);

            // When & Then
            mockMvc.perform(delete("/course/{id}", courseId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("课程删除成功"));

            verify(courseManager).deleteCourse(courseId, testUser);
        }
    }

    @Test
    @DisplayName("Should return bad request when deleting non-existing course")
    void deleteCourse_WithNonExistingId_ShouldReturnBadRequest() throws Exception {
        // Given
        Short courseId = 999;
        try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
            when(courseManager.deleteCourse(courseId, testUser)).thenReturn(false);

            // When & Then
            mockMvc.perform(delete("/course/{id}", courseId))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("课程不存在"));
        }
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
    @DisplayName("Should search all courses when keyword is null")
    void searchCourses_WithoutKeyword_ShouldReturnAllCourses() throws Exception {
        // Given
        List<Course> courses = Arrays.asList(testCourse);
        when(courseManager.getCourses(null)).thenReturn(courses);

        // When & Then
        mockMvc.perform(get("/course/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("搜索课程成功"));

        verify(courseManager).getCourses(null);
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

    @Test
    @DisplayName("Should successfully get courses by type")
    void getCoursesByType_WithValidTypeId_ShouldReturnSuccess() throws Exception {
        // Given
        byte courseTypeId = 13; // MAJOR_REQUIRED
        List<Course> courses = Arrays.asList(testCourse);
        when(courseManager.getCoursesByType(courseTypeId)).thenReturn(courses);

        // When & Then
        mockMvc.perform(get("/course/type/{courseTypeId}", courseTypeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取课程类型列表成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].courseType.id").value(13));

        verify(courseManager).getCoursesByType(courseTypeId);
    }

    @Test
    @DisplayName("Should successfully get courses by department and type")
    void getCoursesByDepartmentAndType_WithValidIds_ShouldReturnSuccess() throws Exception {
        // Given
        Byte departmentId = 1;
        byte courseTypeId = 13;
        List<Course> courses = Arrays.asList(testCourse);
        when(courseManager.getCoursesByDepartmentAndType(departmentId, courseTypeId)).thenReturn(courses);

        // When & Then
        mockMvc.perform(get("/course/department/{departmentId}/type/{courseTypeId}", departmentId, courseTypeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取课程列表成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].department.id").value(1))
                .andExpect(jsonPath("$.data[0].courseType.id").value(13));

        verify(courseManager).getCoursesByDepartmentAndType(departmentId, courseTypeId);
    }

    @Test
    @DisplayName("Should handle runtime exceptions gracefully")
    void handleRuntimeException_ShouldReturnInternalServerError() throws Exception {
        // Given
        when(courseManager.getCourses()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/course"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("获取课程列表失败"));
    }

    @Test
    @DisplayName("Should validate course creation DTO fields")
    void addCourse_WithInvalidFields_ShouldReturnBadRequest() throws Exception {
        // Given - Course with invalid credits (too high)
        CourseCreationDto invalidDto = new CourseCreationDto(
                "Valid Name",
                "CS201",
                (byte) 1,
                (byte) 13,
                new BigDecimal("100.0")); // exceeds max value

        // When & Then
        try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);

            mockMvc.perform(post("/course")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidDto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    @DisplayName("Should validate course update DTO fields")
    void updateCourse_WithInvalidFields_ShouldReturnBadRequest() throws Exception {
        // Given - Course with invalid credits (negative)
        CourseUpdateDto invalidDto = new CourseUpdateDto(
                (short) 1,
                "Valid Name",
                "CS201",
                (byte) 1,
                (byte) 13,
                new BigDecimal("-1.0")); // negative value

        // When & Then
        try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);

            mockMvc.perform(put("/course")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidDto)))
                    .andExpect(status().isBadRequest());
        }
    }
}
