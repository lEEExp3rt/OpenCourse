package org.opencourse.controllers;

import org.opencourse.dto.response.ApiResponse;
import org.opencourse.models.User;
import org.opencourse.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取所有用户列表
     * @param page 页码
     * @param size 每页条数
     * @return 用户列表
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        // 调用UserService获取用户列表
        // 这里需要在UserService中添加一个获取分页用户列表的方法
        // 暂时使用空列表代替，后续可根据需要实现
        
        Map<String, Object> response = new HashMap<>();
        response.put("users", List.of());
        response.put("totalElements", 0);
        response.put("totalPages", 0);
        return ResponseEntity.ok(ApiResponse.success("获取用户列表成功", response));
    }

    /**
     * 更新用户角色
     * @param userId 用户ID
     * @param role 新角色
     * @return 操作结果
     */
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<ApiResponse<Void>> updateUserRole(
            @PathVariable Integer userId,
            @RequestParam User.UserRole role) {
        
        boolean result = userService.updateUserRole(userId, role);
        if (result) {
            return ResponseEntity.ok(ApiResponse.success("用户角色更新成功"));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("用户不存在"));
        }
    }

    /**
     * 禁用用户
     * @param userId 用户ID
     * @return 操作结果
     */
    @PutMapping("/users/{userId}/disable")
    public ResponseEntity<ApiResponse<Void>> disableUser(@PathVariable Integer userId) {
        boolean result = userService.disableUser(userId);
        if (result) {
            return ResponseEntity.ok(ApiResponse.success("用户已禁用"));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("用户不存在"));
        }
    }

    /**
     * 启用用户
     * @param userId 用户ID
     * @return 操作结果
     */
    @PutMapping("/users/{userId}/enable")
    public ResponseEntity<ApiResponse<Void>> enableUser(@PathVariable Integer userId) {
        boolean result = userService.enableUser(userId);
        if (result) {
            return ResponseEntity.ok(ApiResponse.success("用户已启用"));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("用户不存在"));
        }
    }
} 