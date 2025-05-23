package org.opencourse.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opencourse.dto.request.PasswordResetDto;
import org.opencourse.dto.request.UserLoginDto;
import org.opencourse.dto.request.UserRegistrationDto;
import org.opencourse.models.User;
import org.opencourse.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;

import jakarta.mail.MessagingException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    // Mock依赖项，因为我们不想在集成测试中真正发送邮件或操作真实数据库
    @MockBean
    private UserRepo userRepo;

    @MockBean
    private EmailService emailService;
    
    @MockBean
    private VerificationService verificationService;
    
    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    @DisplayName("完整用户注册流程测试")
    void completeRegistrationFlow() throws MessagingException {
        // 1. 首先发送验证码
        String email = "test@example.com";
        when(userRepo.existsByEmail(email)).thenReturn(false);
        when(emailService.sendVerificationCode(eq(email), anyString(), anyString()))
                .thenReturn("123456");
        
        boolean sendCodeResult = userService.sendRegistrationVerificationCode(email);
        assertTrue(sendCodeResult, "验证码发送应该成功");
        
        // 2. 验证验证码并注册用户
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setName("测试用户");
        registrationDto.setEmail(email);
        registrationDto.setPassword("password123");
        registrationDto.setVerificationCode("123456");
        
        when(verificationService.verifyCode(email, "123456")).thenReturn(true);
        User newUser = new User("测试用户", email, "encodedPassword", User.UserRole.USER);
        when(userRepo.save(any(User.class))).thenReturn(newUser);
        
        User registeredUser = userService.registerUser(registrationDto);
        assertNotNull(registeredUser, "注册用户应该成功");
        assertEquals("测试用户", registeredUser.getName());
        assertEquals(email, registeredUser.getEmail());
        
        // 验证验证码应被删除
        verify(verificationService).removeVerificationCode(email);
    }

    @Test
    @DisplayName("用户登录流程测试")
    void loginFlow() {
        // 1. 尝试登录
        String email = "test@example.com";
        String password = "password123";
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setEmail(email);
        loginDto.setPassword(password);
        
        User user = new User("测试用户", email, "encodedPassword", User.UserRole.USER);
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        
        String token = userService.login(loginDto);
        assertNotNull(token, "登录应该返回令牌");
        
        // 2. 登录失败测试
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("认证失败"));
        
        token = userService.login(loginDto);
        assertNull(token, "认证失败应该返回null");
    }

    @Test
    @DisplayName("密码重置流程测试")
    void passwordResetFlow() throws MessagingException {
        // 1. 首先发送密码重置验证码
        String email = "test@example.com";
        when(userRepo.existsByEmail(email)).thenReturn(true);
        when(emailService.sendVerificationCode(eq(email), anyString(), anyString()))
                .thenReturn("123456");
        
        boolean sendCodeResult = userService.sendPasswordResetVerificationCode(email);
        assertTrue(sendCodeResult, "密码重置验证码发送应该成功");
        
        // 2. 验证验证码并重置密码
        PasswordResetDto resetDto = new PasswordResetDto();
        resetDto.setEmail(email);
        resetDto.setNewPassword("newPassword123");
        resetDto.setVerificationCode("123456");
        
        when(verificationService.verifyCode(email, "123456")).thenReturn(true);
        User user = new User("测试用户", email, "oldEncodedPassword", User.UserRole.USER);
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(user));
        
        boolean resetResult = userService.resetPassword(resetDto);
        assertTrue(resetResult, "密码重置应该成功");
        
        // 验证用户应被保存且验证码应被删除
        verify(userRepo).save(user);
        verify(verificationService).removeVerificationCode(email);
    }

    @Test
    @DisplayName("用户角色管理流程测试")
    void userRoleManagementFlow() {
        // 1. 更新用户角色
        int userId = 1;
        User user = new User("测试用户", "test@example.com", "encodedPassword", User.UserRole.USER);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        
        boolean updateRoleResult = userService.updateUserRole(userId, User.UserRole.ADMIN);
        assertTrue(updateRoleResult, "更新角色应该成功");
        assertEquals(User.UserRole.ADMIN, user.getRole(), "用户角色应该被更新为ADMIN");
        
        // 验证用户应被保存
        verify(userRepo).save(user);
        
        // 2. 禁用用户
        reset(userRepo);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        
        boolean disableResult = userService.disableUser(userId);
        assertTrue(disableResult, "禁用用户应该成功");
        assertEquals(0, user.getActivity(), "用户活跃度应该被设置为0");
        
        // 验证用户应被保存
        verify(userRepo).save(user);
        
        // 3. 启用用户
        reset(userRepo);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        
        boolean enableResult = userService.enableUser(userId);
        assertTrue(enableResult, "启用用户应该成功");
        assertEquals(1, user.getActivity(), "用户活跃度应该被设置为1");
        
        // 验证用户应被保存
        verify(userRepo).save(user);
    }
} 