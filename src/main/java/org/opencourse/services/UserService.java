package org.opencourse.services;

import jakarta.mail.MessagingException;
import org.opencourse.dto.request.PasswordResetDto;
import org.opencourse.dto.request.UserLoginDto;
import org.opencourse.dto.request.UserRegistrationDto;
import org.opencourse.models.User;

import java.util.Optional;

/**
 * 用户服务接口，定义用户相关的业务逻辑
 */
public interface UserService {
    
    /**
     * 发送注册验证码
     * @param email 用户邮箱
     * @return 操作结果
     * @throws MessagingException 邮件发送异常
     */
    boolean sendRegistrationVerificationCode(String email) throws MessagingException;
    
    /**
     * 注册用户
     * @param registrationDto 注册信息
     * @return 注册后的用户实体，如果注册失败则返回null
     */
    User registerUser(UserRegistrationDto registrationDto);
    
    /**
     * 用户登录
     * @param loginDto 登录信息
     * @return JWT令牌，如果登录失败则返回null
     */
    String login(UserLoginDto loginDto);
    
    /**
     * 发送密码重置验证码
     * @param email 用户邮箱
     * @return 操作结果
     * @throws MessagingException 邮件发送异常
     */
    boolean sendPasswordResetVerificationCode(String email) throws MessagingException;
    
    /**
     * 重置密码
     * @param resetDto 重置信息
     * @return 操作结果
     */
    boolean resetPassword(PasswordResetDto resetDto);
    
    /**
     * 获取用户信息
     * @param email 用户邮箱
     * @return 用户信息
     */
    Optional<User> getUserByEmail(String email);
    
    /**
     * 更新用户角色
     * @param userId 用户ID
     * @param role 新角色
     * @return 操作结果
     */
    boolean updateUserRole(Integer userId, User.UserRole role);
    
    /**
     * 禁用用户
     * @param userId 用户ID
     * @return 操作结果
     */
    boolean disableUser(Integer userId);
    
    /**
     * 启用用户
     * @param userId 用户ID
     * @return 操作结果
     */
    boolean enableUser(Integer userId);
}
