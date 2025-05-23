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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false) // 禁用Spring Security过滤器，便于测试
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        // 设置用户对象
        user = new User("测试用户", "test@example.com", 
                "encodedPassword", User.UserRole.USER);
        
        // 用反射设置用户ID (因为User类中没有直接设置ID的方法)
        try {
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, 1);
            
            // 设置创建时间
            java.lang.reflect.Field createdAtField = User.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(user, LocalDateTime.now());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // 模拟认证上下文
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("test@example.com");
    }

    @Test
    @DisplayName("获取当前登录用户信息 - 成功")
    void getCurrentUser_Success() throws Exception {
        // 安排
        when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));

        // 执行并断言
        mockMvc.perform(get("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("获取用户信息成功"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("测试用户"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.role").value("USER"))
                .andExpect(jsonPath("$.data.activity").value(1));
    }

    @Test
    @DisplayName("获取当前登录用户信息 - 用户不存在")
    void getCurrentUser_UserNotFound() throws Exception {
        // 安排
        when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.empty());

        // 执行并断言
        mockMvc.perform(get("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户不存在"));
    }

    @Test
    @DisplayName("更新用户角色 - 成功")
    void updateUserRole_Success() throws Exception {
        // 安排
        when(userService.updateUserRole(eq(1), eq(User.UserRole.ADMIN))).thenReturn(true);

        // 执行并断言
        mockMvc.perform(put("/api/users/1/role")
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
        mockMvc.perform(put("/api/users/1/role")
                .param("role", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户不存在"));
    }
} 