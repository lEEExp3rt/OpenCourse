package org.opencourse.services;

import org.opencourse.models.Interaction;
import org.opencourse.models.Course;
import org.opencourse.models.User;
import org.opencourse.repositories.InteractionRepo;
import org.opencourse.repositories.CourseRepo;
import org.opencourse.repositories.UserRepo;
import org.opencourse.utils.typeinfo.ActionType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Interaction service manager.
 * 
 * @author !EEExp3rt
 * @author LJX
 */
@Service
public class InteractionManager {

    private final InteractionRepo interactionRepo; // Data access object.
    private final CourseRepo courseRepo;
    private final UserRepo userRepo;
    private final HistoryManager historyManager;

    /**
     * Constructor.
     * 
     * @param interactionRepo The interaction repository.
     * @param courseRepo The course repository.
     * @param userRepo The user repository.
     * @param historyManager The history manager.
     */
    @Autowired
    public InteractionManager(InteractionRepo interactionRepo, CourseRepo courseRepo, UserRepo userRepo, 
            HistoryManager historyManager) {
        this.interactionRepo = interactionRepo;
        this.courseRepo = courseRepo;
        this.userRepo = userRepo;
        this.historyManager = historyManager;
    }

    /**
     * Add an interaction comment.
     * 
     * @param courseId The ID of the course.
     * @param userId The ID of the user.
     * @param content The content of the comment.
     * @param rating The rating of the course (1-10).
     * @return The created interaction.
     */
    @Transactional
    public Interaction addInteraction(Short courseId, Integer userId, String content, Byte rating) {
        // 验证评分范围
        if (rating != null && (rating < 1 || rating > 10)) {
            throw new IllegalArgumentException("评分必须在1-10之间");
        }
        
        // 获取课程和用户
        Optional<Course> courseOpt = courseRepo.findById(courseId);
        Optional<User> userOpt = userRepo.findById(userId);
        
        if (courseOpt.isEmpty() || userOpt.isEmpty()) {
            throw new IllegalArgumentException("课程或用户不存在");
        }
        
        Course course = courseOpt.get();
        User user = userOpt.get();
            
        // 检查用户是否已经对该课程进行过评论
        Interaction existingInteraction = interactionRepo.findByCourseAndUser(course, user);
        if (existingInteraction != null) {
            // 更新现有评论
            if (content != null) {
                existingInteraction.setContent(content);
            }
            if (rating != null) {
                existingInteraction.setRating(rating);
            }
            return interactionRepo.save(existingInteraction);
        }
        
        // 创建新评论
        Interaction interaction = new Interaction(course, user, content, rating);
        Interaction savedInteraction = interactionRepo.save(interaction);
        
        // 添加创建评论的历史记录
        historyManager.addHistory(user, ActionType.CREATE_INTERACTION, savedInteraction);
        
        // 如果包含评分，添加评分的历史记录
        if (rating != null) {
            historyManager.addHistory(user, ActionType.RATE_COURSE, savedInteraction);
        }
        
        return savedInteraction;
    }

    /**
     * Get all interaction comments.
     * 
     * @return List of all interactions.
     */
    public List<Interaction> getInteractions() {
        return interactionRepo.findAll();
    }
    
    /**
     * Get all interaction comments for a specific course.
     * 
     * @param courseId The ID of the course.
     * @return List of interactions for the course.
     */
    public List<Interaction> getInteractionsByCourse(Short courseId) {
        Optional<Course> courseOpt = courseRepo.findById(courseId);
        if (courseOpt.isEmpty()) {
            return List.of(); // 返回空列表
        }
        return interactionRepo.findByCourse(courseOpt.get());
    }
    
    /**
     * Get all interaction comments by a specific user.
     * 
     * @param userId The ID of the user.
     * @return List of interactions by the user.
     */
    public List<Interaction> getInteractionsByUser(Integer userId) {
        Optional<User> userOpt = userRepo.findById(userId);
        if (userOpt.isEmpty()) {
            return List.of(); // 返回空列表
        }
        return interactionRepo.findByUser(userOpt.get());
    }

    /**
     * Like a comment.
     * 
     * @param interactionId The ID of the interaction to like.
     * @param userId The ID of the user who likes.
     * @return true if the operation was successful, false otherwise.
     */
    @Transactional
    public boolean likeInteraction(Integer interactionId, Integer userId) {
        Optional<Interaction> interactionOpt = interactionRepo.findById(interactionId);
        Optional<User> userOpt = userRepo.findById(userId);
        
        if (interactionOpt.isEmpty() || userOpt.isEmpty()) {
            return false;
        }
        
        Interaction interaction = interactionOpt.get();
        User user = userOpt.get();
        
        // 检查用户是否已经点赞过
        if (historyManager.hasLiked(user, interaction)) {
            return false; // 已经点赞过，不需要重复操作
        }
        
        // 检查用户是否之前点踩过，如果是则先取消点踩
        if (historyManager.hasDisliked(user, interaction)) {
            historyManager.removeAction(user, ActionType.DISLIKE_INTERACTION, interaction);
            interaction.undislikes();
        }
        
        // 添加点赞记录
        historyManager.addHistory(user, ActionType.LIKE_INTERACTION, interaction);
        
        // 增加点赞数
        interaction.likes();
        interactionRepo.save(interaction);
        
        return true;
    }

    /**
     * Unlike a comment.
     * 
     * @param interactionId The ID of the interaction to unlike.
     * @param userId The ID of the user who unlikes.
     * @return true if the operation was successful, false otherwise.
     */
    @Transactional
    public boolean unlikeInteraction(Integer interactionId, Integer userId) {
        Optional<Interaction> interactionOpt = interactionRepo.findById(interactionId);
        Optional<User> userOpt = userRepo.findById(userId);
        
        if (interactionOpt.isEmpty() || userOpt.isEmpty()) {
            return false;
        }
        
        Interaction interaction = interactionOpt.get();
        User user = userOpt.get();
        
        // 检查用户是否点赞过
        if (!historyManager.hasLiked(user, interaction)) {
            return false; // 没有点赞过，无需取消
        }
        
        // 移除点赞记录
        historyManager.removeAction(user, ActionType.LIKE_INTERACTION, interaction);
        
        // 减少点赞数
        interaction.unlikes();
        interactionRepo.save(interaction);
        
        return true;
    }

    /**
     * Dislike a comment.
     * 
     * @param interactionId The ID of the interaction to dislike.
     * @param userId The ID of the user who dislikes.
     * @return true if the operation was successful, false otherwise.
     */
    @Transactional
    public boolean dislikeInteraction(Integer interactionId, Integer userId) {
        Optional<Interaction> interactionOpt = interactionRepo.findById(interactionId);
        Optional<User> userOpt = userRepo.findById(userId);
        
        if (interactionOpt.isEmpty() || userOpt.isEmpty()) {
            return false;
        }
        
        Interaction interaction = interactionOpt.get();
        User user = userOpt.get();
        
        // 检查用户是否已经点踩过
        if (historyManager.hasDisliked(user, interaction)) {
            return false; // 已经点踩过，不需要重复操作
        }
        
        // 检查用户是否之前点赞过，如果是则先取消点赞
        if (historyManager.hasLiked(user, interaction)) {
            historyManager.removeAction(user, ActionType.LIKE_INTERACTION, interaction);
            interaction.unlikes();
        }
        
        // 添加点踩记录
        historyManager.addHistory(user, ActionType.DISLIKE_INTERACTION, interaction);
        
        // 增加点踩数
        interaction.dislikes();
        interactionRepo.save(interaction);
        
        return true;
    }

    /**
     * Undislike a comment.
     * 
     * @param interactionId The ID of the interaction to undislike.
     * @param userId The ID of the user who undislikes.
     * @return true if the operation was successful, false otherwise.
     */
    @Transactional
    public boolean undislikeInteraction(Integer interactionId, Integer userId) {
        Optional<Interaction> interactionOpt = interactionRepo.findById(interactionId);
        Optional<User> userOpt = userRepo.findById(userId);
        
        if (interactionOpt.isEmpty() || userOpt.isEmpty()) {
            return false;
        }
        
        Interaction interaction = interactionOpt.get();
        User user = userOpt.get();
        
        // 检查用户是否点踩过
        if (!historyManager.hasDisliked(user, interaction)) {
            return false; // 没有点踩过，无需取消
        }
        
        // 移除点踩记录
        historyManager.removeAction(user, ActionType.DISLIKE_INTERACTION, interaction);
        
        // 减少点踩数
        interaction.undislikes();
        interactionRepo.save(interaction);
        
        return true;
    }
    
    /**
     * 检查用户是否对评论点赞或点踩
     * 
     * @param interactionId 评论ID
     * @param userId 用户ID
     * @return 包含点赞和点踩状态的数组，[是否点赞, 是否点踩]
     */
    public boolean[] getUserInteractionStatus(Integer interactionId, Integer userId) {
        boolean[] status = new boolean[2]; // [isLiked, isDisliked]
        
        Optional<Interaction> interactionOpt = interactionRepo.findById(interactionId);
        Optional<User> userOpt = userRepo.findById(userId);
        
        if (interactionOpt.isEmpty() || userOpt.isEmpty()) {
            return status;
        }
        
        Interaction interaction = interactionOpt.get();
        User user = userOpt.get();
        
        status[0] = historyManager.hasLiked(user, interaction);
        status[1] = historyManager.hasDisliked(user, interaction);
        
        return status;
    }
}
