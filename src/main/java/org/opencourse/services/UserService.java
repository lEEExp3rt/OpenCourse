package org.opencourse.services;

import jakarta.mail.MessagingException;

import org.opencourse.dto.request.PasswordResetDto;
import org.opencourse.dto.request.UserLoginDto;
import org.opencourse.dto.request.UserRegistrationDto;
import org.opencourse.models.User;
import org.opencourse.repositories.UserRepo;
import org.opencourse.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 用户服务
 */
@Service
public class UserService {

    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final VerificationService verificationService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Autowired
    public UserService(UserRepo userRepository, PasswordEncoder passwordEncoder,
                       EmailService emailService, VerificationService verificationService,
                       AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.verificationService = verificationService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    /**
     * 发送注册验证码
     * @param email 用户邮箱
     * @return 操作结果
     * @throws MessagingException 邮件发送异常
     */
    public boolean sendRegistrationVerificationCode(String email) throws MessagingException {
        // 检查邮箱是否已注册
        if (userRepository.existsByEmail(email)) {
            return false;
        }

        // 发送验证码
        emailService.sendVerificationCode(email, "OpenCourse 注册验证码", "注册账号");
        return true;
    }

    /**
     * 注册用户
     * @param registrationDto 注册信息
     * @return 注册结果
     */
    @Transactional
    public User registerUser(UserRegistrationDto registrationDto) {
        // 验证验证码
        if (!verificationService.verifyCode(registrationDto.getEmail(), registrationDto.getVerificationCode())) {
            return null;
        }

        // 创建新用户
        User user = new User(
            registrationDto.getName(),
            registrationDto.getEmail(),
            passwordEncoder.encode(registrationDto.getPassword()),
            User.UserRole.USER // 默认为普通用户角色
        );

        // 保存用户
        User savedUser = userRepository.save(user);

        // 删除验证码
        verificationService.removeVerificationCode(registrationDto.getEmail());

        return savedUser;
    }

    /**
     * 用户登录
     * @param loginDto 登录信息
     * @return JWT令牌
     */
    public String login(UserLoginDto loginDto) {
        // 进行认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        // 认证成功，更新安全上下文
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 获取用户信息
        Optional<User> userOptional = userRepository.findByEmail(loginDto.getEmail());
        if (userOptional.isEmpty()) {
            return null;
        }

        // 生成JWT令牌
        return jwtUtils.generateToken(userOptional.get());
    }

    /**
     * 发送密码重置验证码
     * @param email 用户邮箱
     * @return 操作结果
     * @throws MessagingException 邮件发送异常
     */
    public boolean sendPasswordResetVerificationCode(String email) throws MessagingException {
        // 检查邮箱是否存在
        if (!userRepository.existsByEmail(email)) {
            return false;
        }

        // 发送验证码
        emailService.sendVerificationCode(email, "OpenCourse 密码重置验证码", "重置密码");
        return true;
    }

    /**
     * 重置密码
     * @param resetDto 重置信息
     * @return 操作结果
     */
    @Transactional
    public boolean resetPassword(PasswordResetDto resetDto) {
        // 验证验证码
        if (!verificationService.verifyCode(resetDto.getEmail(), resetDto.getVerificationCode())) {
            return false;
        }

        // 查找用户
        Optional<User> userOptional = userRepository.findByEmail(resetDto.getEmail());
        if (userOptional.isEmpty()) {
            return false;
        }

        // 更新密码
        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(resetDto.getNewPassword()));
        userRepository.save(user);

        // 删除验证码
        verificationService.removeVerificationCode(resetDto.getEmail());

        return true;
    }

    /**
     * 获取用户信息
     * @param email 用户邮箱
     * @return 用户信息
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * 更新用户角色
     * @param userId 用户ID
     * @param role 新角色
     * @return 操作结果
     */
    @Transactional
    public boolean updateUserRole(Integer userId, User.UserRole role) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return false;
        }

        User user = userOptional.get();
        user.setRole(role);
        userRepository.save(user);
        return true;
    }
}
