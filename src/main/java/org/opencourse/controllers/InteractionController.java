package org.opencourse.controllers;

import org.opencourse.dto.request.InteractionCreationDto;
import org.opencourse.dto.request.InteractionUpdateDto;
import org.opencourse.dto.response.ApiResponse;
import org.opencourse.models.Interaction;
import org.opencourse.models.User;
import org.opencourse.services.InteractionManager;
import org.opencourse.utils.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 交互控制器，处理评论相关请求
 */
@RestController
@RequestMapping("/interaction")
public class InteractionController {

    private final InteractionManager interactionManager;

    @Autowired
    public InteractionController(InteractionManager interactionManager) {
        this.interactionManager = interactionManager;
    }

    /**
     * 添加评论或评分
     * 
     * @param courseId 课程ID
     * @param content 评论内容
     * @param rating 评分（1-10）
     * @return 创建的评论
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> addInteraction(
//            @RequestParam Short courseId,
//            @RequestParam(required = false) String content,
//            @RequestParam(required = false) Byte rating
           @RequestBody InteractionCreationDto interactionCreationDto) {
        
        User user = SecurityUtils.getCurrentUser();
        
        // 添加评论
        try {
//            InteractionCreationDto dto = new InteractionCreationDto(courseId, content, rating);
//            Interaction interaction = interactionManager.addInteraction(dto, user);
            Interaction interaction = interactionManager.addInteraction(interactionCreationDto, user);

            if (interaction == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("您已经对该课程发表过评论"));
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("id", interaction.getId());
            data.put("content", interaction.getContent());
            data.put("rating", interaction.getRating());
            data.put("courseId", interaction.getCourse().getId());
            data.put("userId", interaction.getUser().getId());
            // data.put("userName", interaction.getUser().getName());
            data.put("likes", interaction.getLikes());
            data.put("createdAt", interaction.getCreatedAt());
            
            return ResponseEntity.ok(ApiResponse.success("评论添加成功", data));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 更新评论或评分
     * 
     * @param id 评论ID
     * @param content 评论内容
     * @param rating 评分（1-10）
     * @return 更新的评论
     */
    // @PutMapping("/{id}")
    @PutMapping()
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateInteraction(
            // @PathVariable Integer id,
            @RequestParam Integer id,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) Byte rating) {
        
        // 获取当前登录用户
        User user = SecurityUtils.getCurrentUser();

        // 更新评论
        try {
            InteractionUpdateDto dto = new InteractionUpdateDto(id, content, rating);
            Interaction interaction = interactionManager.updateInteraction(dto, user);
            
            Map<String, Object> data = new HashMap<>();
            data.put("id", interaction.getId());
            data.put("content", interaction.getContent());
            data.put("rating", interaction.getRating());
            data.put("courseId", interaction.getCourse().getId());
            data.put("userId", interaction.getUser().getId());
            // data.put("userName", interaction.getUser().getName());
            data.put("likes", interaction.getLikes());
            data.put("createdAt", interaction.getCreatedAt());
            
            return ResponseEntity.ok(ApiResponse.success("评论更新成功", data));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取指定的评论
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getInteractionById(@PathVariable Integer id) {
        Interaction interaction = interactionManager.getInteractionById(id);
        
        if (interaction == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("id", interaction.getId());
        data.put("content", interaction.getContent());
        data.put("rating", interaction.getRating());
        // data.put("dislikes", interaction.getDislikes());
        data.put("courseId", interaction.getCourse().getId());
        data.put("userId", interaction.getUser().getId());
        // data.put("userName", interaction.getUser().getName());
        data.put("likes", interaction.getLikes());
        data.put("createdAt", interaction.getCreatedAt());
        
        return ResponseEntity.ok(ApiResponse.success("获取指定id评论成功", data));
    }


    /**
     * 获取课程的所有评论
     * 
     * @param courseId 课程ID
     * @return 评论列表
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getInteractionsByCourse(
            @PathVariable Short courseId) {
        
        List<Interaction> interactions = interactionManager.getInteractions(courseId);
        
        // 获取当前登录用户
        User user = SecurityUtils.getCurrentUser();
        Integer userId = user.getId();
        
        List<Map<String, Object>> data = interactions.stream().map(interaction -> {
            Map<String, Object> interactionData = new HashMap<>();
            interactionData.put("id", interaction.getId());
            interactionData.put("content", interaction.getContent());
            interactionData.put("rating", interaction.getRating());
            interactionData.put("likes", interaction.getLikes());
            // interactionData.put("dislikes", interaction.getDislikes());
            interactionData.put("courseId", interaction.getCourse().getId());
            interactionData.put("userId", interaction.getUser().getId());

            // interactionData.put("userName", interaction.getUser().getName());
            interactionData.put("createdAt", interaction.getCreatedAt());
            
            // 添加当前用户是否已点赞/点踩的信息
            try {
                boolean status = interactionManager.getUserInteractionStatus(interaction.getId(), user);
                interactionData.put("isLiked", status);
            } catch (Exception e) {
                interactionData.put("isLiked", false);
            }
            
            // 添加当前用户是否是评论的所有者
            interactionData.put("isOwner", interaction.getUser().getId().equals(userId));
            
            return interactionData;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("获取评论成功", data));
    }

    /**
     * 对评论点赞
     * 
     * @param interactionId 评论ID
     * @return 操作结果
     */
    @PostMapping("/{interactionId}/like")
    public ResponseEntity<ApiResponse<Void>> likeInteraction(@PathVariable Integer interactionId) {
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        
        boolean success = interactionManager.likeInteraction(interactionId, user);
        
        if (success) {
            return ResponseEntity.ok(ApiResponse.success("点赞成功"));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("点赞失败，评论不存在或已点赞"));
        }
    }

    /**
     * 取消对评论的点赞
     * 
     * @param interactionId 评论ID
     * @return 操作结果
     */
    @PostMapping("/{interactionId}/unlike")
    public ResponseEntity<ApiResponse<Void>> unlikeInteraction(@PathVariable Integer interactionId) {
        // 获取当前登录用户
        User user = SecurityUtils.getCurrentUser();
        
        boolean success = interactionManager.unlikeInteraction(interactionId, user);
        
        if (success) {
            return ResponseEntity.ok(ApiResponse.success("取消点赞成功"));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("取消点赞失败，评论不存在或未点赞"));
        }
    }

    /**
     * 删除评论
     * 
     * @param interactionId 评论ID
     * @return 操作结果
     */
    @DeleteMapping("/{interactionId}")
    public ResponseEntity<ApiResponse<Void>> deleteInteraction(@PathVariable Integer interactionId) {
        // 获取当前登录用户
        User user = SecurityUtils.getCurrentUser();
        
        boolean success = interactionManager.deleteInteraction(interactionId, user);
        
        if (success) {
            return ResponseEntity.ok(ApiResponse.success("评论删除成功"));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("删除评论失败，评论不存在或无权限删除"));
        }
    }
} 