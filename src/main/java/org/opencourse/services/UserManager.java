package org.opencourse.services;

import jakarta.mail.MessagingException;

import org.opencourse.dto.request.PasswordResetDto;
import org.opencourse.dto.request.UserLoginDto;
import org.opencourse.dto.request.UserRegistrationDto;
import org.opencourse.models.User;
import org.opencourse.repositories.UserRepo;
import org.opencourse.services.email.EmailService;
import org.opencourse.services.email.VerificationService;
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
 * User service manager.
 * 
 * @author LJX
 */
@Service  // 把当前类注册为spring的一个bean
public class UserManager implements UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final VerificationService verificationService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    /**
     * Constructor.
     * 
     * @param userRepo              User repository.
     * @param passwordEncoder       Password encoder.
     * @param emailService          Email service.
     * @param verificationService   Verification service.
     * @param authenticationManager Authentication manager.
     * @param jwtUtils              JWT utils.
     */
    @Autowired
    public UserManager(
        UserRepo userRepo,
        PasswordEncoder passwordEncoder,
        EmailService emailService,
        VerificationService verificationService,
        AuthenticationManager authenticationManager,
        JwtUtils jwtUtils
    ) {
        this.userRepo = userRepo;
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
    @Override
    public boolean sendRegistrationVerificationCode(String email) throws MessagingException {
        // 检查邮箱是否已注册
        if (userRepo.existsByEmail(email)) {
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
    @Override
    @Transactional
    public User registerUser(UserRegistrationDto registrationDto) {
        // 验证验证码
        if (!verificationService.verifyCode(registrationDto.getEmail(), registrationDto.getVerificationCode())) {
            return null;
        }

        // 创建新用户
        // DTO 是数据传输对象 用于接收前端传入的数据
        // 但是 userRepo 需要的是 User 实体类
        // 所以需要把 DTO 转换为 User 实体类
        User user = new User(
            registrationDto.getName(),
            registrationDto.getEmail(),
            passwordEncoder.encode(registrationDto.getPassword()),
            User.UserRole.USER // 默认为普通用户角色
        );

        // 保存用户
        // 直接调用 UserRepo 的 save 方法 该方法继承自 JpaRepository 接口
        // 该方法会根据实体类的id是否为空 来决定是更新还是插入
        User savedUser = userRepo.save(user);

        // 删除验证码
        verificationService.removeVerificationCode(registrationDto.getEmail());

        return savedUser;
    }

    /**
     * 用户登录
     * @param loginDto 登录信息
     * @return JWT令牌
     */
    @Override
    public String login(UserLoginDto loginDto) {
        try {
        // 进行认证
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );

        // 认证成功，更新安全上下文
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 获取用户信息
        Optional<User> userOptional = userRepo.findByEmail(loginDto.getEmail());
        if (userOptional.isEmpty()) {
            return null;
        }

        // 生成JWT令牌
        return jwtUtils.generateToken(userOptional.get());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 发送密码重置验证码
     * @param email 用户邮箱
     * @return 操作结果
     * @throws MessagingException 邮件发送异常
     */
    @Override
    public boolean sendPasswordResetVerificationCode(String email) throws MessagingException {
        // 检查邮箱是否存在
        if (!userRepo.existsByEmail(email)) {
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
    @Override
    @Transactional
    public boolean resetPassword(PasswordResetDto resetDto) {
        // 验证验证码
        if (!verificationService.verifyCode(resetDto.getEmail(), resetDto.getVerificationCode())) {
            return false;
        }

        // 查找用户
        Optional<User> userOptional = userRepo.findByEmail(resetDto.getEmail());
        if (userOptional.isEmpty()) {
            return false;
        }

        // 更新密码
        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(resetDto.getNewPassword()));
        userRepo.save(user);

        // 删除验证码
        verificationService.removeVerificationCode(resetDto.getEmail());

        return true;
    }

    /**
     * 获取用户信息
     * @param email 用户邮箱
     * @return 用户信息
     */
    @Override
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);
    }

    /**
     * 获取用户信息
     * @param name 用户名
     * @return 用户信息
     */
    public User getUserByName(String name) {
        return userRepo.findByName(name).orElse(null);
    }

    /**
     * 获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    public User getUser(Integer userId) {
        return userRepo.findById(userId).orElse(null);
    }

    /**
     * 更新用户角色
     * @param userId 用户ID
     * @param role 新角色
     * @return 操作结果
     */
    @Override
    @Transactional
    public boolean updateUserRole(Integer userId, User.UserRole role) {
        Optional<User> userOptional = userRepo.findById(userId);
        if (userOptional.isEmpty()) {
            return false;
        }

        User user = userOptional.get();
        user.setRole(role);
        userRepo.save(user);
        return true;
    }
    
    /**
     * 禁用用户
     * @param userId 用户ID
     * @return 操作结果
     */
    @Override
    @Transactional
    public boolean disableUser(Integer userId) {
        Optional<User> userOptional = userRepo.findById(userId);
        if (userOptional.isEmpty()) {
            return false;
        }
        
        User user = userOptional.get();
        user.setActivity(0); // 设置活跃度为0表示禁用
        userRepo.save(user);
        return true;
    }
    
    /**
     * 启用用户
     * @param userId 用户ID
     * @return 操作结果
     */
    @Override
    @Transactional
    public boolean enableUser(Integer userId) {
        Optional<User> userOptional = userRepo.findById(userId);
        if (userOptional.isEmpty()) {
            return false;
        }
        
        User user = userOptional.get();
        user.setActivity(1); // 设置活跃度为1表示启用
        userRepo.save(user);
        return true;
    }
}
