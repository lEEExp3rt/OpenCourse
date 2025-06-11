package org.opencourse.controllers;

import jakarta.validation.Valid;
import org.opencourse.dto.request.ResourceUploadDto;
import org.opencourse.dto.response.ApiResponse;
import org.opencourse.models.Resource;
import org.opencourse.models.User;
import org.opencourse.services.ResourceManager;
import org.opencourse.services.storage.FileInfo;
import org.opencourse.utils.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资源管理控制器
 * 实现资源的增删查、点赞/取消点赞、查看下载等功能
 * 
 * @author 熊经理
 */
@RestController
@RequestMapping("/resource")
public class ResourceController {

    private final ResourceManager resourceManager;

    @Autowired
    public ResourceController(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    /**
     * 新增资源
     * 
     * @param dto  资源上传信息
     * @param file 上传的文件
     * @return 创建结果
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Map<String, Object>>> addResource(
            @Valid @ModelAttribute ResourceUploadDto dto,
            @RequestParam("file") MultipartFile file) {
        try {
            User user = SecurityUtils.getCurrentUser();
            Resource resource = resourceManager.addResource(dto, file, user);

            if (resource == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("资源创建失败"));
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("id", resource.getId());
            responseData.put("name", resource.getName());
            responseData.put("resourceType", resource.getResourceType().getDescription());
            responseData.put("createdAt", resource.getCreatedAt());

            return ResponseEntity.ok(ApiResponse.success("资源创建成功", responseData));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("参数错误: " + e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 删除资源
     * 
     * @param resourceId 资源ID
     * @return 删除结果
     */
    @DeleteMapping("/{resourceId}")
    public ResponseEntity<ApiResponse<Void>> deleteResource(@PathVariable Integer resourceId) {
        try {
            User user = SecurityUtils.getCurrentUser();
            boolean success = resourceManager.deleteResource(resourceId, user);

            if (success) {
                return ResponseEntity.ok(ApiResponse.success("资源删除成功"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("删除资源失败，资源不存在或无权限删除"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 获取单个资源信息
     * 
     * @param resourceId 资源ID
     * @return 资源信息
     */
    @GetMapping("/{resourceId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getResource(@PathVariable Integer resourceId) {
        try {
            Resource resource = resourceManager.getResource(resourceId);

            if (resource == null) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("id", resource.getId());
            responseData.put("name", resource.getName());
            responseData.put("description", resource.getDescription());
            responseData.put("resourceType", resource.getResourceType());
            responseData.put("course", Map.of(
                    "id", resource.getCourse().getId(),
                    "name", resource.getCourse().getName(),
                    "code", resource.getCourse().getCode()));
            responseData.put("user", Map.of(
                    "id", resource.getUser().getId(),
                    "name", resource.getUser().getName()));
            responseData.put("views", resource.getViews());
            responseData.put("likes", resource.getLikes());
            responseData.put("dislikes", resource.getDislikes());
            responseData.put("createdAt", resource.getCreatedAt());

            return ResponseEntity.ok(ApiResponse.success("获取资源信息成功", responseData));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 根据课程ID获取资源列表
     * 
     * @param courseId 课程ID
     * @return 资源列表
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getResourcesByCourse(@PathVariable Short courseId) {
        try {
            List<Resource> resources = resourceManager.getResourcesByCourse(courseId);

            List<Map<String, Object>> responseData = resources.stream().map(resource -> {
                Map<String, Object> data = new HashMap<>();
                data.put("id", resource.getId());
                data.put("name", resource.getName());
                data.put("description", resource.getDescription());
                data.put("resourceTypeId", resource.getResourceType().getId());
                data.put("user", Map.of(
                        "id", resource.getUser().getId(),
                        "name", resource.getUser().getName()));
                data.put("views", resource.getViews());
                data.put("likes", resource.getLikes());
                data.put("createdAt", resource.getCreatedAt());
                return data;
            }).toList();

            return ResponseEntity.ok(ApiResponse.success("获取课程资源列表成功", responseData));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 点赞资源
     * 
     * @param resourceId 资源ID
     * @return 点赞结果
     */
    @PostMapping("/{resourceId}/like")
    public ResponseEntity<ApiResponse<Void>> likeResource(@PathVariable Integer resourceId) {
        try {
            User user = SecurityUtils.getCurrentUser();
            boolean success = resourceManager.likeResource(resourceId, user);

            if (success) {
                return ResponseEntity.ok(ApiResponse.success("点赞成功"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("点赞失败，资源不存在或已点赞"));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("参数错误: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 取消点赞资源
     * 
     * @param resourceId 资源ID
     * @return 取消点赞结果
     */
    @PostMapping("/{resourceId}/unlike")
    public ResponseEntity<ApiResponse<Void>> unlikeResource(@PathVariable Integer resourceId) {
        try {
            User user = SecurityUtils.getCurrentUser();
            boolean success = resourceManager.unlikeResource(resourceId, user);

            if (success) {
                return ResponseEntity.ok(ApiResponse.success("取消点赞成功"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("取消点赞失败，资源不存在或未点赞"));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("参数错误: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 查看/下载资源
     * 
     * @param resourceId 资源ID
     * @return 资源文件流
     */
    @GetMapping("/{resourceId}/view")
    public ResponseEntity<?> viewResource(@PathVariable Integer resourceId) {
        try {
            User user = SecurityUtils.getCurrentUser();
            Resource resource = resourceManager.getResource(resourceId);

            if (resource == null) {
                return ResponseEntity.notFound().build();
            }

            FileInfo fileInfo = resourceManager.viewResource(resourceId, user);

            if (fileInfo == null || fileInfo.getFile() == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("无法获取资源文件"));
            }

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileInfo.getFileName() + "\"");

            // 根据文件类型设置Content-Type
            MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
            if (resource.getResourceFile() != null) {
                switch (resource.getResourceFile().getFileType()) {
                    case PDF:
                        mediaType = MediaType.APPLICATION_PDF;
                        break;
                    case TEXT:
                        mediaType = MediaType.TEXT_PLAIN;
                        break;
                    default:
                        mediaType = MediaType.APPLICATION_OCTET_STREAM;
                        break;
                }
            }

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(mediaType)
                    .body(new InputStreamResource(fileInfo.getFile()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("参数错误: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("服务器内部错误: " + e.getMessage()));
        }
    }

    /**
     * 根据用户ID获取资源列表
     * 
     * @param userId 用户ID
     * @return 资源列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getResourcesByUser(@PathVariable Integer userId) {
        try {
            List<Resource> resources = resourceManager.getResourcesByUser(userId);

            List<Map<String, Object>> responseData = resources.stream().map(resource -> {
                Map<String, Object> data = new HashMap<>();
                data.put("id", resource.getId());
                data.put("name", resource.getName());
                data.put("description", resource.getDescription());
                data.put("resourceType", resource.getResourceType());
                data.put("course", Map.of(
                        "id", resource.getCourse().getId(),
                        "name", resource.getCourse().getName(),
                        "code", resource.getCourse().getCode()));
                data.put("views", resource.getViews());
                data.put("likes", resource.getLikes());
                data.put("createdAt", resource.getCreatedAt());
                return data;
            }).toList();

            return ResponseEntity.ok(ApiResponse.success("获取用户资源列表成功", responseData));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("服务器内部错误: " + e.getMessage()));
        }
    }
}
