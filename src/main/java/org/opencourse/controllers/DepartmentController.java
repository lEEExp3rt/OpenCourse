package org.opencourse.controllers;

import jakarta.validation.Valid;

import org.opencourse.dto.request.DepartmentCreationDto;
import org.opencourse.dto.request.DepartmentUpdateDto;
import org.opencourse.dto.response.ApiResponse;
import org.opencourse.models.Department;
import org.opencourse.models.User;
import org.opencourse.services.DepartmentManager;
import org.opencourse.utils.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DepartmentController.java
 * implementation for managing departments in the application.
 * includes endpoints for creating, updating, deleting and retrieving departments.
 * @author LLLLjx
 */

/**
 * 部门管理控制器
 */
@RestController
@RequestMapping("/department")
public class DepartmentController {

    private final DepartmentManager departmentManager;

    @Autowired
    public DepartmentController(DepartmentManager departmentManager) {
        this.departmentManager = departmentManager;
    }

    /**
     * 新增部门
     * 
     * @param creationDto 部门创建信息
     * @return 创建结果
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> addDepartment(
            @Valid @RequestBody DepartmentCreationDto creationDto) {
        try {
            User user = SecurityUtils.getCurrentUser();
            Department department = departmentManager.addDepartment(creationDto.getName(), user);

            if (department == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("部门名称已存在"));
            }

            Map<String, Object> data = new HashMap<>();
            data.put("id", department.getId());
            data.put("name", department.getName());

            return ResponseEntity.ok(ApiResponse.success("部门创建成功", data));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("创建部门失败"));
        }
    }

    /**
     * 更新部门
     * 
     * @param updateDto 部门更新信息
     * @return 更新结果
     */
    @PutMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateDepartment(
            @Valid @RequestBody DepartmentUpdateDto updateDto) {
        try {
            User user = SecurityUtils.getCurrentUser();
            Department department = departmentManager.updateDepartment(updateDto.getId(), updateDto.getName(), user);

            if (department == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("部门不存在或名称已被使用"));
            }

            Map<String, Object> data = new HashMap<>();
            data.put("id", department.getId());
            data.put("name", department.getName());

            return ResponseEntity.ok(ApiResponse.success("部门更新成功", data));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("更新部门失败"));
        }
    }

    /**
     * 删除部门
     * 
     * @param id 部门ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@PathVariable Byte id) {
        try {
            User user = SecurityUtils.getCurrentUser();
            boolean success = departmentManager.deleteDepartment(id, user);

            if (success) {
                return ResponseEntity.ok(ApiResponse.success("部门删除成功"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("部门不存在或删除失败"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("删除部门失败"));
        }
    }

    /**
     * 根据ID查询部门
     * 
     * @param id 部门ID
     * @return 部门信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDepartmentById(@PathVariable Byte id) {
        try {
            Department department = departmentManager.getDepartment(id);

            if (department == null) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> data = new HashMap<>();
            data.put("id", department.getId());
            data.put("name", department.getName());

            return ResponseEntity.ok(ApiResponse.success("获取部门信息成功", data));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取部门信息失败"));
        }
    }

    /**
     * 获取所有部门
     * 
     * @return 部门列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllDepartments() {
        try {
            List<Department> departments = departmentManager.getDepartments();

            List<Map<String, Object>> data = departments.stream().map(department -> {
                Map<String, Object> departmentData = new HashMap<>();
                departmentData.put("id", department.getId());
                departmentData.put("name", department.getName());
                return departmentData;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("获取部门列表成功", data));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("获取部门列表失败"));
        }
    }

    /**
     * 模糊查询部门
     * 
     * @param name 部门名称关键词（可选）
     * @return 匹配的部门列表
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> searchDepartments(
            @RequestParam(required = false) String name) {
        try {
            List<Department> departments = departmentManager.getDepartments(name);

            List<Map<String, Object>> data = departments.stream().map(department -> {
                Map<String, Object> departmentData = new HashMap<>();
                departmentData.put("id", department.getId());
                departmentData.put("name", department.getName());
                return departmentData;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("搜索部门成功", data));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("搜索部门失败"));
        }
    }
}
