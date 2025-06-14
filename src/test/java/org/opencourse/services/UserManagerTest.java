package org.opencourse.services;

import jakarta.mail.MessagingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opencourse.dto.request.PasswordResetDto;
import org.opencourse.dto.request.UserLoginDto;
import org.opencourse.dto.request.UserRegistrationDto;
import org.opencourse.models.User;
import org.opencourse.repositories.UserRepo;
import org.opencourse.services.email.EmailService;
import org.opencourse.services.email.VerificationService;
import org.opencourse.utils.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 用户管理服务测试类
 */
@ExtendWith(MockitoExtension.class)
public class UserManagerTest {

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
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    private UserManager userManager;

    @BeforeEach
    void setUp() {
        userManager = new UserManager(
                userRepo,
                passwordEncoder,
                emailService,
                verificationService,
                authenticationManager,
                jwtUtils
        );
    }

    @Test
    void testSendRegistrationVerificationCode_WhenEmailNotRegistered_ShouldReturnTrue() throws MessagingException {
        // Arrange
        String email = "new@example.com";
        when(userRepo.existsByEmail(email)).thenReturn(false);
        when(emailService.sendVerificationCode(eq(email), anyString(), anyString())).thenReturn("123456");

        // Act
        boolean result = userManager.sendRegistrationVerificationCode(email);

        // Assert
        assertThat(result).isTrue();
        verify(emailService).sendVerificationCode(eq(email), eq("OpenCourse 注册验证码"), eq("注册账号"));
    }

    @Test
    void testSendRegistrationVerificationCode_WhenEmailAlreadyRegistered_ShouldReturnFalse() throws MessagingException {
        // Arrange
        String email = "existing@example.com";
        when(userRepo.existsByEmail(email)).thenReturn(true);

        // Act
        boolean result = userManager.sendRegistrationVerificationCode(email);

        // Assert
        assertThat(result).isFalse();
        verify(emailService, never()).sendVerificationCode(anyString(), anyString(), anyString());
    }

    @Test
    void testRegisterUser_WhenValidData_ShouldSaveUser() {
        // Arrange
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setName("testuser");
        dto.setEmail("test@example.com");
        dto.setPassword("password123");
        dto.setVerificationCode("123456");

        when(verificationService.verifyCode(dto.getEmail(), dto.getVerificationCode())).thenReturn(true);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");
        
        User expectedUser = new User(dto.getName(), dto.getEmail(), "encodedPassword", User.UserRole.USER);
        when(userRepo.save(any(User.class))).thenReturn(expectedUser);

        // Act
        User result = userManager.registerUser(dto);

        // Assert
        assertThat(result).isNotNull();
        
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(userCaptor.capture());
        
        User capturedUser = userCaptor.getValue();
        assertThat(capturedUser.getName()).isEqualTo(dto.getName());
        assertThat(capturedUser.getEmail()).isEqualTo(dto.getEmail());
        assertThat(capturedUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(capturedUser.getRole()).isEqualTo(User.UserRole.USER);
        
        verify(verificationService).removeVerificationCode(dto.getEmail());
    }
    
    @Test
    void testRegisterUser_WhenInvalidVerificationCode_ShouldReturnNull() {
        // Arrange
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setName("testuser");
        dto.setEmail("test@example.com");
        dto.setPassword("password123");
        dto.setVerificationCode("invalid");

        when(verificationService.verifyCode(dto.getEmail(), dto.getVerificationCode())).thenReturn(false);

        // Act
        User result = userManager.registerUser(dto);

        // Assert
        assertThat(result).isNull();
        verify(userRepo, never()).save(any(User.class));
        verify(verificationService, never()).removeVerificationCode(anyString());
    }

    @Test
    void testLogin_WhenValidCredentials_ShouldReturnToken() {
        // Arrange
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setEmail("user@example.com");
        loginDto.setPassword("password");

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        
        User user = new User("testuser", loginDto.getEmail(), "encodedPassword", User.UserRole.USER);
        when(userRepo.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
        
        String expectedToken = "jwt-token";
        when(jwtUtils.generateToken(user)).thenReturn(expectedToken);

        // Act
        String result = userManager.login(loginDto);

        // Assert
        assertThat(result).isEqualTo(expectedToken);
        verify(authenticationManager).authenticate(
            argThat(authentication -> 
                authentication.getPrincipal().equals(loginDto.getEmail()) && 
                authentication.getCredentials().equals(loginDto.getPassword())
            )
        );
    }

    @Test
    void testLogin_WhenInvalidCredentials_ShouldReturnNull() {
        // Arrange
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setEmail("user@example.com");
        loginDto.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act
        String result = userManager.login(loginDto);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void testLogin_WhenUserNotFound_ShouldReturnNull() {
        // Arrange
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setEmail("nonexistent@example.com");
        loginDto.setPassword("password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepo.findByEmail(loginDto.getEmail())).thenReturn(Optional.empty());

        // Act
        String result = userManager.login(loginDto);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void testSendPasswordResetVerificationCode_WhenEmailExists_ShouldReturnTrue() throws MessagingException {
        // Arrange
        String email = "existing@example.com";
        when(userRepo.existsByEmail(email)).thenReturn(true);
        when(emailService.sendVerificationCode(eq(email), anyString(), anyString())).thenReturn("123456");

        // Act
        boolean result = userManager.sendPasswordResetVerificationCode(email);

        // Assert
        assertThat(result).isTrue();
        verify(emailService).sendVerificationCode(eq(email), eq("OpenCourse 密码重置验证码"), eq("重置密码"));
    }

    @Test
    void testSendPasswordResetVerificationCode_WhenEmailDoesNotExist_ShouldReturnFalse() throws MessagingException {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepo.existsByEmail(email)).thenReturn(false);

        // Act
        boolean result = userManager.sendPasswordResetVerificationCode(email);

        // Assert
        assertThat(result).isFalse();
        verify(emailService, never()).sendVerificationCode(anyString(), anyString(), anyString());
    }

    @Test
    void testResetPassword_WhenValidData_ShouldUpdatePassword() {
        // Arrange
        PasswordResetDto resetDto = new PasswordResetDto();
        resetDto.setEmail("user@example.com");
        resetDto.setNewPassword("newpassword");
        resetDto.setVerificationCode("123456");

        when(verificationService.verifyCode(resetDto.getEmail(), resetDto.getVerificationCode())).thenReturn(true);
        
        User user = new User("testuser", resetDto.getEmail(), "oldEncodedPassword", User.UserRole.USER);
        when(userRepo.findByEmail(resetDto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(resetDto.getNewPassword())).thenReturn("newEncodedPassword");

        // Act
        boolean result = userManager.resetPassword(resetDto);

        // Assert
        assertThat(result).isTrue();
        assertThat(user.getPassword()).isEqualTo("newEncodedPassword");
        verify(userRepo).save(user);
        verify(verificationService).removeVerificationCode(resetDto.getEmail());
    }

    @Test
    void testResetPassword_WhenInvalidVerificationCode_ShouldReturnFalse() {
        // Arrange
        PasswordResetDto resetDto = new PasswordResetDto();
        resetDto.setEmail("user@example.com");
        resetDto.setNewPassword("newpassword");
        resetDto.setVerificationCode("invalid");

        when(verificationService.verifyCode(resetDto.getEmail(), resetDto.getVerificationCode())).thenReturn(false);

        // Act
        boolean result = userManager.resetPassword(resetDto);

        // Assert
        assertThat(result).isFalse();
        verify(userRepo, never()).save(any(User.class));
        verify(verificationService, never()).removeVerificationCode(anyString());
    }

    @Test
    void testResetPassword_WhenUserNotFound_ShouldReturnFalse() {
        // Arrange
        PasswordResetDto resetDto = new PasswordResetDto();
        resetDto.setEmail("nonexistent@example.com");
        resetDto.setNewPassword("newpassword");
        resetDto.setVerificationCode("123456");

        when(verificationService.verifyCode(resetDto.getEmail(), resetDto.getVerificationCode())).thenReturn(true);
        when(userRepo.findByEmail(resetDto.getEmail())).thenReturn(Optional.empty());

        // Act
        boolean result = userManager.resetPassword(resetDto);

        // Assert
        assertThat(result).isFalse();
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void testGetUserByEmail_WhenUserExists_ShouldReturnUser() {
        // Arrange
        String email = "user@example.com";
        User expectedUser = new User("testuser", email, "encodedPassword", User.UserRole.USER);
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(expectedUser));

        // Act
        User result = userManager.getUserByEmail(email);

        // Assert
        assertThat(result).isEqualTo(expectedUser);
    }

    @Test
    void testGetUserByEmail_WhenUserDoesNotExist_ShouldReturnNull() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepo.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        User result = userManager.getUserByEmail(email);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void testGetUserByName_WhenUserExists_ShouldReturnUser() {
        // Arrange
        String name = "testuser";
        User expectedUser = new User(name, "user@example.com", "encodedPassword", User.UserRole.USER);
        when(userRepo.findByName(name)).thenReturn(Optional.of(expectedUser));

        // Act
        User result = userManager.getUserByName(name);

        // Assert
        assertThat(result).isEqualTo(expectedUser);
    }

    @Test
    void testGetUserByName_WhenUserDoesNotExist_ShouldReturnNull() {
        // Arrange
        String name = "nonexistent";
        when(userRepo.findByName(name)).thenReturn(Optional.empty());

        // Act
        User result = userManager.getUserByName(name);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void testGetUser_WhenUserExists_ShouldReturnUser() {
        // Arrange
        Integer userId = 1;
        User expectedUser = new User("testuser", "user@example.com", "encodedPassword", User.UserRole.USER);
        when(userRepo.findById(userId)).thenReturn(Optional.of(expectedUser));

        // Act
        User result = userManager.getUser(userId);

        // Assert
        assertThat(result).isEqualTo(expectedUser);
    }

    @Test
    void testGetUser_WhenUserDoesNotExist_ShouldReturnNull() {
        // Arrange
        Integer userId = 999;
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        // Act
        User result = userManager.getUser(userId);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void testUpdateUserRole_WhenUserExists_ShouldUpdateRoleAndReturnTrue() {
        // Arrange
        Integer userId = 1;
        User user = new User("testuser", "user@example.com", "encodedPassword", User.UserRole.USER);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        // Act
        boolean result = userManager.updateUserRole(userId, User.UserRole.ADMIN);

        // Assert
        assertThat(result).isTrue();
        assertThat(user.getRole()).isEqualTo(User.UserRole.ADMIN);
        verify(userRepo).save(user);
    }

    @Test
    void testUpdateUserRole_WhenUserDoesNotExist_ShouldReturnFalse() {
        // Arrange
        Integer userId = 999;
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        // Act
        boolean result = userManager.updateUserRole(userId, User.UserRole.ADMIN);

        // Assert
        assertThat(result).isFalse();
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void testAddUserActivity_WhenUserExists_ShouldIncreaseActivityAndReturn() {
        // Arrange
        Integer userId = 1;
        User user = new User("testuser", "user@example.com", "encodedPassword", User.UserRole.USER);
        user.setActivity(5); // 初始活跃度为5
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        // Act
        Integer result = userManager.addUserActivity(userId);

        // Assert
        assertThat(result).isEqualTo(6);
        assertThat(user.getActivity()).isEqualTo(6);
    }

    @Test
    void testAddUserActivity_WhenUserDoesNotExist_ShouldReturnZero() {
        // Arrange
        Integer userId = 999;
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        // Act
        Integer result = userManager.addUserActivity(userId);

        // Assert
        assertThat(result).isEqualTo(0);
    }

    @Test
    void testGetUserActivity_WhenUserExists_ShouldReturnActivity() {
        // Arrange
        Integer userId = 1;
        User user = new User("testuser", "user@example.com", "encodedPassword", User.UserRole.USER);
        user.setActivity(10);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        // Act
        Integer result = userManager.getUserActivity(userId);

        // Assert
        assertThat(result).isEqualTo(10);
    }

    @Test
    void testGetUserActivity_WhenUserDoesNotExist_ShouldReturnZero() {
        // Arrange
        Integer userId = 999;
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        // Act
        Integer result = userManager.getUserActivity(userId);

        // Assert
        assertThat(result).isEqualTo(0);
    }

    @Test
    void testReduceUserActivity_WhenUserExists_ShouldDecreaseActivityAndReturn() {
        // Arrange
        Integer userId = 1;
        User user = new User("testuser", "user@example.com", "encodedPassword", User.UserRole.USER);
        user.setActivity(5); // 初始活跃度为5
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        // Act
        Integer result = userManager.reduceUserActivity(userId);

        // Assert
        assertThat(result).isEqualTo(4);
        assertThat(user.getActivity()).isEqualTo(4);
    }

    @Test
    void testReduceUserActivity_WhenUserDoesNotExist_ShouldReturnZero() {
        // Arrange
        Integer userId = 999;
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        // Act
        Integer result = userManager.reduceUserActivity(userId);

        // Assert
        assertThat(result).isEqualTo(0);
    }

    // @Test
    // void testDisableUser_WhenUserExists_ShouldSetActivityToZeroAndReturnTrue() {
    //     // Arrange
    //     Integer userId = 1;
    //     User user = new User("testuser", "user@example.com", "encodedPassword", User.UserRole.USER);
    //     when(userRepo.findById(userId)).thenReturn(Optional.of(user));

    //     // Act
    //     boolean result = userManager.disableUser(userId);

    //     // Assert
    //     assertThat(result).isTrue();
    //     assertThat(user.getActivity()).isEqualTo(0);
    //     verify(userRepo).save(user);
    // }

    // @Test
    // void testDisableUser_WhenUserDoesNotExist_ShouldReturnFalse() {
    //     // Arrange
    //     Integer userId = 999;
    //     when(userRepo.findById(userId)).thenReturn(Optional.empty());

    //     // Act
    //     boolean result = userManager.disableUser(userId);

    //     // Assert
    //     assertThat(result).isFalse();
    //     verify(userRepo, never()).save(any(User.class));
    // }

    // @Test
    // void testEnableUser_WhenUserExists_ShouldSetActivityToOneAndReturnTrue() {
    //     // Arrange
    //     Integer userId = 1;
    //     User user = new User("testuser", "user@example.com", "encodedPassword", User.UserRole.USER);
    //     user.setActivity(0); // 先禁用
    //     when(userRepo.findById(userId)).thenReturn(Optional.of(user));

    //     // Act
    //     boolean result = userManager.enableUser(userId);

    //     // Assert
    //     assertThat(result).isTrue();
    //     assertThat(user.getActivity()).isEqualTo(1);
    //     verify(userRepo).save(user);
    // }

    // @Test
    // void testEnableUser_WhenUserDoesNotExist_ShouldReturnFalse() {
    //     // Arrange
    //     Integer userId = 999;
    //     when(userRepo.findById(userId)).thenReturn(Optional.empty());

    //     // Act
    //     boolean result = userManager.enableUser(userId);

    //     // Assert
    //     assertThat(result).isFalse();
    //     verify(userRepo, never()).save(any(User.class));
    // }
} 