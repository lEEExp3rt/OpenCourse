package org.opencourse.controllers;

import org.opencourse.dto.response.ApiResponse;
import org.opencourse.models.User;
import org.opencourse.services.UserManager;
import org.opencourse.utils.JwtUtils;
import org.opencourse.utils.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController   // 接口方法可以直接返回对象 并且对象会被自动转换为json
@RequestMapping("/user")  // localhost:8080/user/**
public class UserController {

    // private final UserService userService;
    private final UserManager userManager;
    private final JwtUtils jwtUtils;

    @Autowired  // 自动注入userService 之前已经在UserManager中注册为bean
    // public UserController(UserService userService) {
    //     this.userService = userService;
    // }
    public UserController(UserManager userManager, JwtUtils jwtUtils) {
        this.userManager = userManager;
        this.jwtUtils = jwtUtils;
    }

    /**
     * 获取当前登录用户信息
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser(@PathVariable String id) {
        User user = SecurityUtils.getCurrentUser();
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        userData.put("role", user.getRole().name());
        userData.put("activity", user.getActivity());
        userData.put("createdAt", user.getCreatedAt());
        userData.put("updatedAt", user.getUpdatedAt());
        userData.put("token", jwtUtils.generateToken(user));
        
        return ResponseEntity.ok(ApiResponse.success("获取用户信息成功", userData));
    }

    /**
     * 更新用户角色（仅管理员可用）
     * @param userId 用户ID
     * @param role 新角色
     * @return 操作结果
     */
    @PutMapping("/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateUserRole(
            @PathVariable Integer userId,
            @RequestParam User.UserRole role) {
        
        // boolean result = userService.updateUserRole(userId, role);
        boolean result = userManager.updateUserRole(userId, role);
        if (result) {
            return ResponseEntity.ok(ApiResponse.success("用户角色更新成功"));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("用户不存在"));
        }
    }
}
