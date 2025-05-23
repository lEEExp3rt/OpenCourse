package org.opencourse.controllers;

import org.opencourse.dto.response.ApiResponse;
import org.opencourse.models.Interaction;
import org.opencourse.models.User;
import org.opencourse.services.InteractionManager;

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
@RequestMapping("/api/interactions")
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
            @RequestParam Short courseId,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) Byte rating) {
        
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        
        // 添加评论
        try {
            Interaction interaction = interactionManager.addInteraction(courseId, user.getId(), content, rating);
            
            Map<String, Object> data = new HashMap<>();
            data.put("id", interaction.getId());
            data.put("content", interaction.getContent());
            data.put("rating", interaction.getRating());
            data.put("courseId", interaction.getCourse().getId());
            data.put("userId", interaction.getUser().getId());
            data.put("userName", interaction.getUser().getName());
            data.put("createdAt", interaction.getCreatedAt());
            
            return ResponseEntity.ok(ApiResponse.success("评论添加成功", data));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
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
        
        List<Interaction> interactions = interactionManager.getInteractionsByCourse(courseId);
        
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Integer userId = user.getId();
        
        List<Map<String, Object>> data = interactions.stream().map(interaction -> {
            Map<String, Object> interactionData = new HashMap<>();
            interactionData.put("id", interaction.getId());
            interactionData.put("content", interaction.getContent());
            interactionData.put("rating", interaction.getRating());
            interactionData.put("likes", interaction.getLikes());
            interactionData.put("dislikes", interaction.getDislikes());
            interactionData.put("userId", interaction.getUser().getId());
            interactionData.put("userName", interaction.getUser().getName());
            interactionData.put("createdAt", interaction.getCreatedAt());
            
            // 添加当前用户是否已点赞/点踩的信息
            try {
                var record = interactionManager.getUserInteractionRecord(interaction.getId(), userId);
                interactionData.put("isLiked", record.isLiked());
                interactionData.put("isDisliked", record.isDisliked());
            } catch (Exception e) {
                interactionData.put("isLiked", false);
                interactionData.put("isDisliked", false);
            }
            
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
        
        interactionManager.likeInteraction(interactionId, user.getId());
        return ResponseEntity.ok(ApiResponse.success("点赞成功"));
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        
        interactionManager.unlikeInteraction(interactionId, user.getId());
        return ResponseEntity.ok(ApiResponse.success("取消点赞成功"));
    }

    /**
     * 对评论点踩
     * 
     * @param interactionId 评论ID
     * @return 操作结果
     */
    @PostMapping("/{interactionId}/dislike")
    public ResponseEntity<ApiResponse<Void>> dislikeInteraction(@PathVariable Integer interactionId) {
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        
        interactionManager.dislikeInteraction(interactionId, user.getId());
        return ResponseEntity.ok(ApiResponse.success("点踩成功"));
    }

    /**
     * 取消对评论的点踩
     * 
     * @param interactionId 评论ID
     * @return 操作结果
     */
    @PostMapping("/{interactionId}/undislike")
    public ResponseEntity<ApiResponse<Void>> undislikeInteraction(@PathVariable Integer interactionId) {
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        
        interactionManager.undislikeInteraction(interactionId, user.getId());
        return ResponseEntity.ok(ApiResponse.success("取消点踩成功"));
    }
} 