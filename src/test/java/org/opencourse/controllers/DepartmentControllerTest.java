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
import org.opencourse.dto.request.DepartmentCreationDto;
import org.opencourse.dto.request.DepartmentUpdateDto;
import org.opencourse.models.Department;
import org.opencourse.models.User;
import org.opencourse.services.DepartmentManager;
import org.opencourse.utils.security.SecurityUtils;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for {@link DepartmentController}.
 * 
 * @author Lee X ALEX
 */
@WebMvcTest(DepartmentController.class)
@Import(TestSecurityConfig.class)
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DepartmentManager departmentManager;

    private User testUser;
    private Department testDepartment;

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
        // Mock the ID using spy
        testDepartment = spy(testDepartment);
        when(testDepartment.getId()).thenReturn((byte) 1);
    }

    @Test
    @DisplayName("Should successfully create department with valid request")
    void addDepartment_WithValidRequest_ShouldReturnSuccess() throws Exception {
        // Given
        DepartmentCreationDto request = new DepartmentCreationDto("Computer Science");

        // Mock SecurityUtils to return test user
        try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
            when(departmentManager.addDepartment("Computer Science", testUser)).thenReturn(testDepartment);

            // When & Then
            mockMvc.perform(post("/department")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("部门创建成功"))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.name").value("Computer Science"));

            verify(departmentManager).addDepartment("Computer Science", testUser);
        }
    }

    @Test
    @DisplayName("Should return bad request when department name is blank")
    void addDepartment_WithBlankName_ShouldReturnBadRequest() throws Exception {
        // Given
        DepartmentCreationDto request = new DepartmentCreationDto("");

        // When & Then
        mockMvc.perform(post("/department")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("部门名称不能为空"));

        verify(departmentManager, never()).addDepartment(anyString(), any());
    }

    @Test
    @DisplayName("Should return bad request when department name is too long")
    void addDepartment_WithTooLongName_ShouldReturnBadRequest() throws Exception {
        // Given
        String longName = "A".repeat(32); // Exceeds 31 character limit
        DepartmentCreationDto request = new DepartmentCreationDto(longName);

        // When & Then
        mockMvc.perform(post("/department")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("部门名称太长"));

        verify(departmentManager, never()).addDepartment(anyString(), any());
    }

    @Test
    @DisplayName("Should return bad request when department already exists")
    void addDepartment_WhenDepartmentExists_ShouldReturnBadRequest() throws Exception {
        // Given
        DepartmentCreationDto request = new DepartmentCreationDto("Existing Department");

        try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
            when(departmentManager.addDepartment("Existing Department", testUser)).thenReturn(null);

            // When & Then
            mockMvc.perform(post("/department")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("部门名称已存在"));

            verify(departmentManager).addDepartment("Existing Department", testUser);
        }
    }

    @Test
    @DisplayName("Should successfully update department with valid request")
    void updateDepartment_WithValidRequest_ShouldReturnSuccess() throws Exception {
        // Given
        DepartmentUpdateDto request = new DepartmentUpdateDto((byte) 1, "Updated Department");

        try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
            when(departmentManager.updateDepartment((byte) 1, "Updated Department", testUser))
                    .thenReturn(testDepartment);

            // When & Then
            mockMvc.perform(put("/department")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("部门更新成功"))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.name").value("Computer Science"));

            verify(departmentManager).updateDepartment((byte) 1, "Updated Department", testUser);
        }
    }

    @Test
    @DisplayName("Should return bad request when updating non-existent department")
    void updateDepartment_WhenDepartmentNotExists_ShouldReturnBadRequest() throws Exception {
        // Given
        DepartmentUpdateDto request = new DepartmentUpdateDto((byte) 99, "Non-existent Department");

        try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
            when(departmentManager.updateDepartment((byte) 99, "Non-existent Department", testUser)).thenReturn(null);

            // When & Then
            mockMvc.perform(put("/department")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("部门不存在或名称已被使用"));

            verify(departmentManager).updateDepartment((byte) 99, "Non-existent Department", testUser);
        }
    }

    @Test
    @DisplayName("Should return bad request when update request has null ID")
    void updateDepartment_WithNullId_ShouldReturnBadRequest() throws Exception {
        // Given
        DepartmentUpdateDto request = new DepartmentUpdateDto(null, "Some Department");

        // When & Then
        mockMvc.perform(put("/department")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("部门ID不能为空"));

        verify(departmentManager, never()).updateDepartment(any(), anyString(), any());
    }

    @Test
    @DisplayName("Should successfully delete department")
    void deleteDepartment_WithValidId_ShouldReturnSuccess() throws Exception {
        // Given
        try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
            when(departmentManager.deleteDepartment((byte) 1, testUser)).thenReturn(true);

            // When & Then
            mockMvc.perform(delete("/department/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("部门删除成功"))
                    .andExpect(jsonPath("$.data").doesNotExist());

            verify(departmentManager).deleteDepartment((byte) 1, testUser);
        }
    }

    @Test
    @DisplayName("Should return bad request when deleting non-existent department")
    void deleteDepartment_WhenDepartmentNotExists_ShouldReturnBadRequest() throws Exception {
        // Given
        try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
            when(departmentManager.deleteDepartment((byte) 99, testUser)).thenReturn(false);

            // When & Then
            mockMvc.perform(delete("/department/99"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("部门不存在"));

            verify(departmentManager).deleteDepartment((byte) 99, testUser);
        }
    }

    @Test
    @DisplayName("Should successfully get department by ID")
    void getDepartmentById_WithValidId_ShouldReturnDepartment() throws Exception {
        // Given
        when(departmentManager.getDepartment((byte) 1)).thenReturn(testDepartment);

        // When & Then
        mockMvc.perform(get("/department/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取部门信息成功"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Computer Science"));

        verify(departmentManager).getDepartment((byte) 1);
    }

    @Test
    @DisplayName("Should return not found when getting non-existent department")
    void getDepartmentById_WhenDepartmentNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(departmentManager.getDepartment((byte) 99)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/department/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("部门不存在"));

        verify(departmentManager).getDepartment((byte) 99);
    }

    @Test
    @DisplayName("Should successfully get all departments")
    void getAllDepartments_ShouldReturnAllDepartments() throws Exception {
        // Given
        Department department2 = new Department("Mathematics");
        department2 = spy(department2);
        when(department2.getId()).thenReturn((byte) 2);

        List<Department> departments = Arrays.asList(testDepartment, department2);
        when(departmentManager.getDepartments()).thenReturn(departments);

        // When & Then
        mockMvc.perform(get("/department"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取部门列表成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Computer Science"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].name").value("Mathematics"));

        verify(departmentManager).getDepartments();
    }

    @Test
    @DisplayName("Should return empty list when no departments exist")
    void getAllDepartments_WhenNoDepartments_ShouldReturnEmptyList() throws Exception {
        // Given
        when(departmentManager.getDepartments()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/department"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取部门列表成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));

        verify(departmentManager).getDepartments();
    }

    @Test
    @DisplayName("Should successfully search departments with keyword")
    void searchDepartments_WithKeyword_ShouldReturnMatchingDepartments() throws Exception {
        // Given
        List<Department> departments = Arrays.asList(testDepartment);
        when(departmentManager.getDepartments("Computer")).thenReturn(departments);

        // When & Then
        mockMvc.perform(get("/department/search")
                .param("name", "Computer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("搜索部门成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Computer Science"));

        verify(departmentManager).getDepartments("Computer");
    }

    @Test
    @DisplayName("Should return all departments when searching without keyword")
    void searchDepartments_WithoutKeyword_ShouldReturnAllDepartments() throws Exception {
        // Given
        Department department2 = new Department("Mathematics");
        department2 = spy(department2);
        when(department2.getId()).thenReturn((byte) 2);

        List<Department> departments = Arrays.asList(testDepartment, department2);
        when(departmentManager.getDepartments()).thenReturn(departments);

        // When & Then
        mockMvc.perform(get("/department/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("搜索部门成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));

        verify(departmentManager).getDepartments();
        verify(departmentManager, never()).getDepartments(anyString());
    }

    @Test
    @DisplayName("Should return empty list when search finds no matches")
    void searchDepartments_WithNoMatches_ShouldReturnEmptyList() throws Exception {
        // Given
        when(departmentManager.getDepartments("NonExistent")).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/department/search")
                .param("name", "NonExistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("搜索部门成功"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));

        verify(departmentManager).getDepartments("NonExistent");
    }

    @Test
    @DisplayName("Should handle internal server error gracefully")
    void addDepartment_WhenInternalError_ShouldReturnInternalServerError() throws Exception {
        // Given
        DepartmentCreationDto request = new DepartmentCreationDto("Test Department");

        try (var mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(testUser);
            when(departmentManager.addDepartment("Test Department", testUser))
                    .thenThrow(new RuntimeException("Database error"));

            // When & Then
            mockMvc.perform(post("/department")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("服务器内部错误"));

            verify(departmentManager).addDepartment("Test Department", testUser);
        }
    }
}
