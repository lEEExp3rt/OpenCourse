package org.opencourse.controllers;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

import org.opencourse.dto.request.PasswordResetDto;
import org.opencourse.dto.request.UserLoginDto;
import org.opencourse.dto.request.UserRegistrationDto;
import org.opencourse.dto.response.ApiResponse;
import org.opencourse.models.User;
import org.opencourse.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 用户认证控制器
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 发送注册验证码
     * @param email 用户邮箱
     * @return 操作结果
     */
    @PostMapping("/register/send-code")
    public ResponseEntity<ApiResponse<Void>> sendRegistrationVerificationCode(@RequestParam String email) {
        try {
            boolean result = userService.sendRegistrationVerificationCode(email);
            if (result) {
                return ResponseEntity.ok(ApiResponse.success("验证码已发送，请注意查收"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("邮箱已被注册"));
            }
        } catch (MessagingException e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("验证码发送失败"));
        }
    }

    /**
     * 用户注册
     * @param registrationDto 注册信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@Valid @RequestBody UserRegistrationDto registrationDto) {
        User user = userService.registerUser(registrationDto);
        if (user == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("注册失败，验证码错误或已过期"));
        }

        // 返回注册成功的用户信息
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        return ResponseEntity.ok(ApiResponse.success("注册成功", userData));
    }

    /**
     * 用户登录
     * @param loginDto 登录信息
     * @return 登录结果
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@Valid @RequestBody UserLoginDto loginDto) {
        String token = userService.login(loginDto);
        if (token == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("用户名或密码错误"));
        }

        // 获取用户信息
        Optional<User> userOptional = userService.getUserByEmail(loginDto.getEmail());
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("用户不存在"));
        }

        User user = userOptional.get();
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole().name()
        ));

        return ResponseEntity.ok(ApiResponse.success("登录成功", result));
    }

    /**
     * 发送密码重置验证码
     * @param email 用户邮箱
     * @return 操作结果
     */
    @PostMapping("/password/send-reset-code")
    public ResponseEntity<ApiResponse<Void>> sendPasswordResetVerificationCode(@RequestParam String email) {
        try {
            boolean result = userService.sendPasswordResetVerificationCode(email);
            if (result) {
                return ResponseEntity.ok(ApiResponse.success("验证码已发送，请注意查收"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("邮箱不存在"));
            }
        } catch (MessagingException e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("验证码发送失败"));
        }
    }

    /**
     * 重置密码
     * @param resetDto 重置信息
     * @return 操作结果
     */
    @PostMapping("/password/reset")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody PasswordResetDto resetDto) {
        boolean result = userService.resetPassword(resetDto);
        if (result) {
            return ResponseEntity.ok(ApiResponse.success("密码重置成功"));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("密码重置失败，验证码错误或已过期"));
        }
    }
}
