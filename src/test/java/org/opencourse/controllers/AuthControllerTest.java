package org.opencourse.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opencourse.dto.request.PasswordResetDto;
import org.opencourse.dto.request.UserLoginDto;
import org.opencourse.dto.request.UserRegistrationDto;
import org.opencourse.models.User;
import org.opencourse.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // 禁用Spring Security过滤器，便于测试
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserRegistrationDto registrationDto;
    private UserLoginDto loginDto;
    private PasswordResetDto resetDto;
    private User user;

    @BeforeEach
    void setUp() {
        // 设置注册DTO
        registrationDto = new UserRegistrationDto();
        registrationDto.setName("测试用户");
        registrationDto.setEmail("test@example.com");
        registrationDto.setPassword("password123");
        registrationDto.setVerificationCode("123456");

        // 设置登录DTO
        loginDto = new UserLoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("password123");

        // 设置密码重置DTO
        resetDto = new PasswordResetDto();
        resetDto.setEmail("test@example.com");
        resetDto.setNewPassword("newPassword123");
        resetDto.setVerificationCode("123456");

        // 设置用户对象
        user = new User("测试用户", "test@example.com", 
                "encodedPassword", User.UserRole.USER);
        
        // 用反射设置用户ID (因为User类中没有直接设置ID的方法)
        try {
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("发送注册验证码 - 成功")
    void sendRegistrationVerificationCode_Success() throws Exception {
        // 安排
        when(userService.sendRegistrationVerificationCode("test@example.com")).thenReturn(true);

        // 执行并断言
        mockMvc.perform(post("/api/auth/register/send-code")
                .param("email", "test@example.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("验证码已发送，请注意查收"));
    }

    @Test
    @DisplayName("发送注册验证码 - 邮箱已被注册")
    void sendRegistrationVerificationCode_EmailExists() throws Exception {
        // 安排
        when(userService.sendRegistrationVerificationCode("test@example.com")).thenReturn(false);

        // 执行并断言
        mockMvc.perform(post("/api/auth/register/send-code")
                .param("email", "test@example.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("邮箱已被注册"));
    }

    @Test
    @DisplayName("发送注册验证码 - 邮件发送异常")
    void sendRegistrationVerificationCode_MessagingException() throws Exception {
        // 安排
        when(userService.sendRegistrationVerificationCode("test@example.com"))
                .thenThrow(new MessagingException("邮件发送失败"));

        // 执行并断言
        mockMvc.perform(post("/api/auth/register/send-code")
                .param("email", "test@example.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("验证码发送失败"));
    }

    @Test
    @DisplayName("注册用户 - 成功")
    void register_Success() throws Exception {
        // 安排
        when(userService.registerUser(Mockito.any(UserRegistrationDto.class))).thenReturn(user);

        // 执行并断言
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("注册成功"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("测试用户"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }

    @Test
    @DisplayName("注册用户 - 验证码错误")
    void register_InvalidCode() throws Exception {
        // 安排
        when(userService.registerUser(any(UserRegistrationDto.class))).thenReturn(null);

        // 执行并断言
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("注册失败，验证码错误或已过期"));
    }

    @Test
    @DisplayName("用户登录 - 成功")
    void login_Success() throws Exception {
        // 安排
        when(userService.login(any(UserLoginDto.class))).thenReturn("jwtToken");
        when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));

        // 执行并断言
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.data.token").value("jwtToken"))
                .andExpect(jsonPath("$.data.user.id").value(1))
                .andExpect(jsonPath("$.data.user.name").value("测试用户"))
                .andExpect(jsonPath("$.data.user.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.user.role").value("USER"));
    }

    @Test
    @DisplayName("用户登录 - 认证失败")
    void login_AuthenticationFailed() throws Exception {
        // 安排
        when(userService.login(any(UserLoginDto.class))).thenReturn(null);

        // 执行并断言
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户名或密码错误"));
    }

    @Test
    @DisplayName("用户登录 - 用户不存在")
    void login_UserNotFound() throws Exception {
        // 安排
        when(userService.login(any(UserLoginDto.class))).thenReturn("jwtToken");
        when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.empty());

        // 执行并断言
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户不存在"));
    }

    @Test
    @DisplayName("发送密码重置验证码 - 成功")
    void sendPasswordResetVerificationCode_Success() throws Exception {
        // 安排
        when(userService.sendPasswordResetVerificationCode("test@example.com")).thenReturn(true);

        // 执行并断言
        mockMvc.perform(post("/api/auth/password/send-reset-code")
                .param("email", "test@example.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("验证码已发送，请注意查收"));
    }

    @Test
    @DisplayName("发送密码重置验证码 - 邮箱不存在")
    void sendPasswordResetVerificationCode_EmailNotExists() throws Exception {
        // 安排
        when(userService.sendPasswordResetVerificationCode("test@example.com")).thenReturn(false);

        // 执行并断言
        mockMvc.perform(post("/api/auth/password/send-reset-code")
                .param("email", "test@example.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("邮箱不存在"));
    }

    @Test
    @DisplayName("发送密码重置验证码 - 邮件发送异常")
    void sendPasswordResetVerificationCode_MessagingException() throws Exception {
        // 安排
        when(userService.sendPasswordResetVerificationCode("test@example.com"))
                .thenThrow(new MessagingException("邮件发送失败"));

        // 执行并断言
        mockMvc.perform(post("/api/auth/password/send-reset-code")
                .param("email", "test@example.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("验证码发送失败"));
    }

    @Test
    @DisplayName("重置密码 - 成功")
    void resetPassword_Success() throws Exception {
        // 安排
        when(userService.resetPassword(any(PasswordResetDto.class))).thenReturn(true);

        // 执行并断言
        mockMvc.perform(post("/api/auth/password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("密码重置成功"));
    }

    @Test
    @DisplayName("重置密码 - 验证码错误或已过期")
    void resetPassword_InvalidCode() throws Exception {
        // 安排
        when(userService.resetPassword(any(PasswordResetDto.class))).thenReturn(false);

        // 执行并断言
        mockMvc.perform(post("/api/auth/password/reset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("密码重置失败，验证码错误或已过期"));
    }
} 