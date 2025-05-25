package org.opencourse.services;

import org.opencourse.models.Interaction;
import org.opencourse.dto.request.InteractionCreationDto;
import org.opencourse.models.Course;
import org.opencourse.models.User;
import org.opencourse.repositories.InteractionRepo;
import org.opencourse.repositories.CourseRepo;
import org.opencourse.repositories.UserRepo;

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

    private final InteractionRepo interactionRepo;
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
    public InteractionManager(
        InteractionRepo interactionRepo,
        CourseRepo courseRepo,
        UserRepo userRepo,
        HistoryManager historyManager
    ) {
        this.interactionRepo = interactionRepo;
        this.courseRepo = courseRepo;
        this.userRepo = userRepo;
        this.historyManager = historyManager;
    }

    /**
     * Add an interaction comment.
     * 
     * @param dto The interaction creation DTO.
     * @return The created interaction.
     */
    @Transactional
    public Interaction addInteraction(InteractionCreationDto dto) {
        // 获取课程和用户
        Course course = courseRepo.findById(dto.getCourseId())
            .orElseThrow(() -> new IllegalArgumentException("课程不存在"));
        User user = userRepo.findById(dto.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        String content = dto.getContent();
        Byte rating = dto.getRating();

        // 检查用户是否已经对该课程进行过评论
        if (interactionRepo.existsByCourseAndUser(course, user)) {
            return null;
        }

        // 创建新评论
        Interaction interaction = interactionRepo.save(new Interaction(course, user, content, rating));

        // 添加创建评论的历史记录
        historyManager.logCreateInteraction(user, interaction);

        // 如果包含评分，添加评分的历史记录
        if (rating != null) {
            historyManager.logRateCourse(user, course);
        }

        return interaction;
    }

    /**
     * Update an interaction comment.
     * 
     * @return
     */
    @Transactional
    public Interaction updateInteraction() {
        return null; // TODO: Implement this method.
    }

    /**
     * Delete an interaction comment.
     * 
     * @param id The ID of the interaction to delete.
     * @param userId The ID of the user who deletes the comment.
     * @return True if the operation was successful, false otherwise.
     */
    @Transactional
    public boolean deleteInteraction(Integer id, Integer userId) {
        User user = userRepo.findById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        Interaction interaction = interactionRepo.findById(id).orElse(null);
        if (interaction == null) {
            return false;
        }
        interactionRepo.delete(interaction);
        // 添加删除评论的历史记录
        historyManager.logDeleteInteraction(user, interaction);
        return true;
    }

    /**
     * Get all interaction comments of a course.
     * 
     * @param courseId The ID of the course.
     * @return List of all interactions.
     */
    public List<Interaction> getInteractions(Short courseId) {
        return interactionRepo.findAllByCourseId(courseId); // TODO: Order.
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
        if (historyManager.getLikeStatus(user, interaction)) {
            return false; // 已经点赞过，不需要重复操作
        }

        // 添加点赞记录
        historyManager.logLikeInteraction(user, interaction);

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
        if (!historyManager.getLikeStatus(user, interaction)) {
            return false; // 没有点赞过，无需取消
        }

        // 移除点赞记录
        historyManager.logUnlikeInteraction(user, interaction);

        // 减少点赞数
        interaction.unlikes();
        interactionRepo.save(interaction);

        return true;
    }

    /**
     * 检查用户是否对评论点赞
     * 
     * @param interactionId 评论ID
     * @param userId 用户ID
     * @return 是否点赞
     */
    public boolean getUserInteractionStatus(Integer interactionId, Integer userId) {
        Optional<Interaction> interactionOpt = interactionRepo.findById(interactionId);
        Optional<User> userOpt = userRepo.findById(userId);

        if (interactionOpt.isEmpty() || userOpt.isEmpty()) {
            return false;
        }

        Interaction interaction = interactionOpt.get();
        User user = userOpt.get();

        return historyManager.getLikeStatus(user, interaction);
    }
}
