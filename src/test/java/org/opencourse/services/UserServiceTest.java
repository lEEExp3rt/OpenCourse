package org.opencourse.services;

import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opencourse.dto.request.PasswordResetDto;
import org.opencourse.dto.request.UserLoginDto;
import org.opencourse.dto.request.UserRegistrationDto;
import org.opencourse.models.User;
import org.opencourse.repositories.UserRepo;
import org.opencourse.utils.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private VerificationService verificationService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserManager userManager; // 实现了UserService接口的具体类

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
    }

    @Test
    @DisplayName("发送注册验证码 - 成功")
    void sendRegistrationVerificationCode_Success() throws MessagingException {
        // 安排
        when(userRepo.existsByEmail("test@example.com")).thenReturn(false);
        when(emailService.sendVerificationCode(eq("test@example.com"), anyString(), anyString()))
                .thenReturn("123456");

        // 执行
        boolean result = userManager.sendRegistrationVerificationCode("test@example.com");

        // 断言
        assertTrue(result);
        verify(emailService).sendVerificationCode(eq("test@example.com"), anyString(), anyString());
    }

    @Test
    @DisplayName("发送注册验证码 - 邮箱已存在")
    void sendRegistrationVerificationCode_EmailExists() throws MessagingException {
        // 安排
        when(userRepo.existsByEmail("test@example.com")).thenReturn(true);

        // 执行
        boolean result = userManager.sendRegistrationVerificationCode("test@example.com");

        // 断言
        assertFalse(result);
        verify(emailService, never()).sendVerificationCode(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("注册用户 - 成功")
    void registerUser_Success() {
        // 安排
        when(verificationService.verifyCode("test@example.com", "123456")).thenReturn(true);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepo.save(any(User.class))).thenReturn(user);

        // 执行
        User result = userManager.registerUser(registrationDto);

        // 断言
        assertNotNull(result);
        assertEquals("测试用户", result.getName());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepo).save(any(User.class));
        verify(verificationService).removeVerificationCode("test@example.com");
    }

    @Test
    @DisplayName("注册用户 - 验证码错误")
    void registerUser_InvalidCode() {
        // 安排
        when(verificationService.verifyCode("test@example.com", "123456")).thenReturn(false);

        // 执行
        User result = userManager.registerUser(registrationDto);

        // 断言
        assertNull(result);
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    @DisplayName("用户登录 - 成功")
    void login_Success() {
        // 安排
        UsernamePasswordAuthenticationToken authToken = 
            new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());
        when(authenticationManager.authenticate(argThat(auth -> 
            auth.getPrincipal().equals(loginDto.getEmail()) && 
            auth.getCredentials().equals(loginDto.getPassword()))))
            .thenReturn(authentication);
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtUtils.generateToken(user)).thenReturn("jwtToken");

        // 执行
        String token = userManager.login(loginDto);

        // 断言
        assertEquals("jwtToken", token);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("用户登录 - 认证失败")
    void login_AuthenticationFailed() {
        // 安排
        when(authenticationManager.authenticate(any()))
            .thenThrow(new RuntimeException("认证失败"));

        // 执行
        String token = userManager.login(loginDto);

        // 断言
        assertNull(token);
    }

    @Test
    @DisplayName("发送密码重置验证码 - 成功")
    void sendPasswordResetVerificationCode_Success() throws MessagingException {
        // 安排
        when(userRepo.existsByEmail("test@example.com")).thenReturn(true);
        when(emailService.sendVerificationCode(eq("test@example.com"), anyString(), anyString()))
                .thenReturn("123456");

        // 执行
        boolean result = userManager.sendPasswordResetVerificationCode("test@example.com");

        // 断言
        assertTrue(result);
        verify(emailService).sendVerificationCode(eq("test@example.com"), anyString(), anyString());
    }

    @Test
    @DisplayName("发送密码重置验证码 - 邮箱不存在")
    void sendPasswordResetVerificationCode_EmailNotExists() throws MessagingException {
        // 安排
        when(userRepo.existsByEmail("test@example.com")).thenReturn(false);

        // 执行
        boolean result = userManager.sendPasswordResetVerificationCode("test@example.com");

        // 断言
        assertFalse(result);
        verify(emailService, never()).sendVerificationCode(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("重置密码 - 成功")
    void resetPassword_Success() {
        // 安排
        when(verificationService.verifyCode("test@example.com", "123456")).thenReturn(true);
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");

        // 执行
        boolean result = userManager.resetPassword(resetDto);

        // 断言
        assertTrue(result);
        verify(userRepo).save(user);
        verify(verificationService).removeVerificationCode("test@example.com");
    }

    @Test
    @DisplayName("重置密码 - 验证码错误")
    void resetPassword_InvalidCode() {
        // 安排
        when(verificationService.verifyCode("test@example.com", "123456")).thenReturn(false);

        // 执行
        boolean result = userManager.resetPassword(resetDto);

        // 断言
        assertFalse(result);
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    @DisplayName("重置密码 - 用户不存在")
    void resetPassword_UserNotFound() {
        // 安排
        when(verificationService.verifyCode("test@example.com", "123456")).thenReturn(true);
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // 执行
        boolean result = userManager.resetPassword(resetDto);

        // 断言
        assertFalse(result);
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    @DisplayName("获取用户信息 - 成功")
    void getUserByEmail_Success() {
        // 安排
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // 执行
        Optional<User> result = userManager.getUserByEmail("test@example.com");

        // 断言
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    @DisplayName("获取用户信息 - 用户不存在")
    void getUserByEmail_UserNotFound() {
        // 安排
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // 执行
        Optional<User> result = userManager.getUserByEmail("test@example.com");

        // 断言
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("更新用户角色 - 成功")
    void updateUserRole_Success() {
        // 安排
        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        // 执行
        boolean result = userManager.updateUserRole(1, User.UserRole.ADMIN);

        // 断言
        assertTrue(result);
        assertEquals(User.UserRole.ADMIN, user.getRole());
        verify(userRepo).save(user);
    }

    @Test
    @DisplayName("更新用户角色 - 用户不存在")
    void updateUserRole_UserNotFound() {
        // 安排
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        // 执行
        boolean result = userManager.updateUserRole(1, User.UserRole.ADMIN);

        // 断言
        assertFalse(result);
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    @DisplayName("禁用用户 - 成功")
    void disableUser_Success() {
        // 安排
        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        // 执行
        boolean result = userManager.disableUser(1);

        // 断言
        assertTrue(result);
        assertEquals(0, user.getActivity());
        verify(userRepo).save(user);
    }

    @Test
    @DisplayName("禁用用户 - 用户不存在")
    void disableUser_UserNotFound() {
        // 安排
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        // 执行
        boolean result = userManager.disableUser(1);

        // 断言
        assertFalse(result);
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    @DisplayName("启用用户 - 成功")
    void enableUser_Success() {
        // 安排
        user.setActivity(0); // 先将用户设置为禁用状态
        when(userRepo.findById(1)).thenReturn(Optional.of(user));

        // 执行
        boolean result = userManager.enableUser(1);

        // 断言
        assertTrue(result);
        assertEquals(1, user.getActivity());
        verify(userRepo).save(user);
    }

    @Test
    @DisplayName("启用用户 - 用户不存在")
    void enableUser_UserNotFound() {
        // 安排
        when(userRepo.findById(1)).thenReturn(Optional.empty());

        // 执行
        boolean result = userManager.enableUser(1);

        // 断言
        assertFalse(result);
        verify(userRepo, never()).save(any(User.class));
    }
} 