package org.opencourse.controllers;

import org.opencourse.dto.response.ApiResponse;
import org.opencourse.models.User;
import org.opencourse.services.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController   // 接口方法可以直接返回对象 并且对象会被自动转换为json
@RequestMapping("/api/users")  // localhost:8080/api/users/**
public class UserController {

    // private final UserService userService;
    private final UserManager userManager;

    @Autowired  // 自动注入userService 之前已经在UserManager中注册为bean
    // public UserController(UserService userService) {
    //     this.userService = userService;
    // }
    public UserController(UserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * 获取当前登录用户信息
     * @return 用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        User user = userManager.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("用户不存在"));
        }
        
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        userData.put("role", user.getRole().name());
        userData.put("activity", user.getActivity());
        userData.put("createdAt", user.getCreatedAt());
        
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
