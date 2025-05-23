package org.opencourse.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opencourse.models.User;
import org.opencourse.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false) // 禁用Spring Security过滤器，便于测试
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        // 预设置，如果需要的话
    }

    @Test
    @DisplayName("获取所有用户列表")
    void getAllUsers() throws Exception {
        // 执行并断言
        mockMvc.perform(get("/api/admin/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取用户列表成功"))
                .andExpect(jsonPath("$.data.users").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(0))
                .andExpect(jsonPath("$.data.totalPages").value(0));
    }

    @Test
    @DisplayName("更新用户角色 - 成功")
    void updateUserRole_Success() throws Exception {
        // 安排
        when(userService.updateUserRole(eq(1), eq(User.UserRole.ADMIN))).thenReturn(true);

        // 执行并断言
        mockMvc.perform(put("/api/admin/users/1/role")
                .param("role", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("用户角色更新成功"));
    }

    @Test
    @DisplayName("更新用户角色 - 用户不存在")
    void updateUserRole_UserNotFound() throws Exception {
        // 安排
        when(userService.updateUserRole(anyInt(), eq(User.UserRole.ADMIN))).thenReturn(false);

        // 执行并断言
        mockMvc.perform(put("/api/admin/users/1/role")
                .param("role", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户不存在"));
    }

    @Test
    @DisplayName("禁用用户 - 成功")
    void disableUser_Success() throws Exception {
        // 安排
        when(userService.disableUser(eq(1))).thenReturn(true);

        // 执行并断言
        mockMvc.perform(put("/api/admin/users/1/disable")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("用户已禁用"));
    }

    @Test
    @DisplayName("禁用用户 - 用户不存在")
    void disableUser_UserNotFound() throws Exception {
        // 安排
        when(userService.disableUser(anyInt())).thenReturn(false);

        // 执行并断言
        mockMvc.perform(put("/api/admin/users/1/disable")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户不存在"));
    }

    @Test
    @DisplayName("启用用户 - 成功")
    void enableUser_Success() throws Exception {
        // 安排
        when(userService.enableUser(eq(1))).thenReturn(true);

        // 执行并断言
        mockMvc.perform(put("/api/admin/users/1/enable")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("用户已启用"));
    }

    @Test
    @DisplayName("启用用户 - 用户不存在")
    void enableUser_UserNotFound() throws Exception {
        // 安排
        when(userService.enableUser(anyInt())).thenReturn(false);

        // 执行并断言
        mockMvc.perform(put("/api/admin/users/1/enable")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户不存在"));
    }
} 