package org.opencourse.services;

import org.opencourse.models.Interaction;
import org.opencourse.models.Course;
import org.opencourse.models.User;
import org.opencourse.models.UserInteractionRecord;
import org.opencourse.repositories.InteractionRepo;
import org.opencourse.repositories.CourseRepo;
import org.opencourse.repositories.UserRepo;
import org.opencourse.repositories.UserInteractionRecordRepo;
import org.opencourse.exceptions.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Interaction service manager.
 * 
 * @author !EEExp3rt
 */
@Service
public class InteractionManager {

    private final InteractionRepo interactionRepo; // Data access object.
    private final CourseRepo courseRepo;
    private final UserRepo userRepo;
    private final UserInteractionRecordRepo userInteractionRecordRepo;

    /**
     * Constructor.
     * 
     * @param interactionRepo The interaction repository.
     * @param courseRepo The course repository.
     * @param userRepo The user repository.
     * @param userInteractionRecordRepo The user interaction record repository.
     */
    @Autowired
    public InteractionManager(InteractionRepo interactionRepo, CourseRepo courseRepo, UserRepo userRepo, 
            UserInteractionRecordRepo userInteractionRecordRepo) {
        this.interactionRepo = interactionRepo;
        this.courseRepo = courseRepo;
        this.userRepo = userRepo;
        this.userInteractionRecordRepo = userInteractionRecordRepo;
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
        Course course = courseRepo.findById(courseId)
            .orElseThrow(() -> new ResourceNotFoundException("未找到课程"));
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("未找到用户"));
            
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
        return interactionRepo.save(interaction);
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
        Course course = courseRepo.findById(courseId)
            .orElseThrow(() -> new ResourceNotFoundException("未找到课程"));
        return interactionRepo.findByCourse(course);
    }
    
    /**
     * Get all interaction comments by a specific user.
     * 
     * @param userId The ID of the user.
     * @return List of interactions by the user.
     */
    public List<Interaction> getInteractionsByUser(Integer userId) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("未找到用户"));
        return interactionRepo.findByUser(user);
    }

    /**
     * Like a comment.
     * 
     * @param interactionId The ID of the interaction to like.
     * @param userId The ID of the user who likes.
     */
    @Transactional
    public void likeInteraction(Integer interactionId, Integer userId) {
        Interaction interaction = interactionRepo.findById(interactionId)
            .orElseThrow(() -> new ResourceNotFoundException("未找到评论"));
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("未找到用户"));
            
        // 查找或创建交互记录
        UserInteractionRecord record = userInteractionRecordRepo.findByUserAndInteraction(user, interaction)
            .orElseGet(() -> new UserInteractionRecord(user, interaction));
            
        record.like();
        userInteractionRecordRepo.save(record);
    }

    /**
     * Unlike a comment.
     * 
     * @param interactionId The ID of the interaction to unlike.
     * @param userId The ID of the user who unlikes.
     */
    @Transactional
    public void unlikeInteraction(Integer interactionId, Integer userId) {
        Interaction interaction = interactionRepo.findById(interactionId)
            .orElseThrow(() -> new ResourceNotFoundException("未找到评论"));
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("未找到用户"));
            
        // 查找交互记录
        Optional<UserInteractionRecord> recordOpt = userInteractionRecordRepo.findByUserAndInteraction(user, interaction);
        if (recordOpt.isPresent()) {
            UserInteractionRecord record = recordOpt.get();
            record.unlike();
            userInteractionRecordRepo.save(record);
        }
    }

    /**
     * Dislike a comment.
     * 
     * @param interactionId The ID of the interaction to dislike.
     * @param userId The ID of the user who dislikes.
     */
    @Transactional
    public void dislikeInteraction(Integer interactionId, Integer userId) {
        Interaction interaction = interactionRepo.findById(interactionId)
            .orElseThrow(() -> new ResourceNotFoundException("未找到评论"));
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("未找到用户"));
            
        // 查找或创建交互记录
        UserInteractionRecord record = userInteractionRecordRepo.findByUserAndInteraction(user, interaction)
            .orElseGet(() -> new UserInteractionRecord(user, interaction));
            
        record.dislike();
        userInteractionRecordRepo.save(record);
    }

    /**
     * Undislike a comment.
     * 
     * @param interactionId The ID of the interaction to undislike.
     * @param userId The ID of the user who undislikes.
     */
    @Transactional
    public void undislikeInteraction(Integer interactionId, Integer userId) {
        Interaction interaction = interactionRepo.findById(interactionId)
            .orElseThrow(() -> new ResourceNotFoundException("未找到评论"));
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("未找到用户"));
            
        // 查找交互记录
        Optional<UserInteractionRecord> recordOpt = userInteractionRecordRepo.findByUserAndInteraction(user, interaction);
        if (recordOpt.isPresent()) {
            UserInteractionRecord record = recordOpt.get();
            record.undislike();
            userInteractionRecordRepo.save(record);
        }
    }
    
    /**
     * 获取用户对评论的互动状态
     * 
     * @param interactionId 评论ID
     * @param userId 用户ID
     * @return 用户互动记录
     */
    public UserInteractionRecord getUserInteractionRecord(Integer interactionId, Integer userId) {
        Interaction interaction = interactionRepo.findById(interactionId)
            .orElseThrow(() -> new ResourceNotFoundException("未找到评论"));
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("未找到用户"));
            
        return userInteractionRecordRepo.findByUserAndInteraction(user, interaction)
            .orElse(new UserInteractionRecord(user, interaction));
    }
}
