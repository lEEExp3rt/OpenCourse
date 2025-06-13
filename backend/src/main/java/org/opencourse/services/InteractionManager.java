package org.opencourse.services;

import org.opencourse.models.Interaction;
import org.opencourse.configs.ApplicationConfig;
import org.opencourse.dto.request.InteractionCreationDto;
import org.opencourse.dto.request.InteractionUpdateDto;
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
    private final ApplicationConfig applicationConfig;
    private final HistoryManager historyManager;

    /**
     * Constructor.
     * 
     * @param interactionRepo The interaction repository.
     * @param courseRepo The course repository.
     * @param userRepo The user repository.
     * @param applicationConfig The application configuration.
     * @param historyManager The history manager.
     */
    @Autowired
    public InteractionManager(
        InteractionRepo interactionRepo,
        CourseRepo courseRepo,
        UserRepo userRepo,
        ApplicationConfig applicationConfig,
        HistoryManager historyManager
    ) {
        this.interactionRepo = interactionRepo;
        this.courseRepo = courseRepo;
        this.userRepo = userRepo;
        this.applicationConfig = applicationConfig;
        this.historyManager = historyManager;
    }

    /**
     * Add an interaction comment.
     * 
     * @param dto The interaction creation DTO.
     * @param user The adder.
     * @return The created interaction.
     */
    @Transactional
    public Interaction addInteraction(InteractionCreationDto dto, User user) {
        // 获取课程
        Course course = courseRepo.findById(dto.getCourseId())
            .orElseThrow(() -> new IllegalArgumentException("课程不存在"));
        String content = dto.getContent();
        Byte rating = dto.getRating();

        // 检查用户是否已经对该课程进行过评论
        Optional<Interaction> existingInteraction = interactionRepo.findByCourseAndUser(course, user);
        if (existingInteraction.isPresent()) {
            // 如果已经存在评论，则更新评论内容和评分
            Interaction interaction = existingInteraction.get();
            if (content != null) {
                interaction.setContent(content);
            }
            if (rating != null) {
                interaction.setRating(rating);
            }

            interaction = interactionRepo.save(interaction);

            // 添加更新评论的历史记录
            historyManager.logUpdateInteraction(user, interaction);

            // 如果包含评分，添加评分的历史记录
            if (rating != null) {
                historyManager.logRateCourse(user, course);
            }

            return interaction;
        }

        // 创建新评论
        Interaction interaction = interactionRepo.save(new Interaction(course, user, content, rating));

        // 添加创作者活跃度
        user.addActivity(applicationConfig.getActivity().getInteraction().getAdd());
        userRepo.save(user);

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
     * @param dto 更新评论的DTO
     * @param user 当前用户
     * @return 更新后的评论
     * @throws IllegalArgumentException 如果评论不存在或者用户不匹配
     */
    @Transactional
    public Interaction updateInteraction(InteractionUpdateDto dto, User user) {
        // 获取评论
        Interaction interaction = interactionRepo.findById(dto.getId())
            .orElseThrow(() -> new IllegalArgumentException("评论不存在"));

        // 检查是否是评论的所有者
        if (!interaction.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("您只能修改自己的评论");
        }

        // 更新评论内容和评分
        if (dto.getContent() != null) {
            interaction.setContent(dto.getContent());
        }

        if (dto.getRating() != null) {
            interaction.setRating(dto.getRating());

            // 添加评分的历史记录
            historyManager.logRateCourse(user, interaction.getCourse());
        }

        // 保存更新后的评论
        interaction = interactionRepo.save(interaction);

        // 添加更新评论的历史记录
        historyManager.logUpdateInteraction(user, interaction);

        return interaction;
    }

    /**
     * Delete an interaction comment.
     * 
     * @param id The ID of the interaction to delete.
     * @param user The deleter.
     * @return True if the operation was successful, false otherwise.
     */
    @Transactional
    public boolean deleteInteraction(Integer id, User user) {
        Interaction interaction = interactionRepo.findById(id).orElse(null);
        if (interaction == null) {
            return false;
        }

        // 检查是否是评论的所有者或管理员
        if (!interaction.getUser().getId().equals(user.getId()) && user.getRole() != User.UserRole.ADMIN) {
            return false;
        }

        interactionRepo.delete(interaction);
        // 减少评论者的活跃度
        user.addActivity(applicationConfig.getActivity().getInteraction().getDelete());
        userRepo.save(user);
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
        return interactionRepo.findByCourseIdOrderByLikesDescCreatedAtDesc(courseId);
    }

    /**
     * Get all interaction comments by a specific user.
     * 
     * @param userId The user ID.
     * @return List of interactions by the user.
     */
    public List<Interaction> getInteractionsByUser(Integer userId) {
        return interactionRepo.findByUserId(userId);
    }

    /**
     * Like a comment.
     * 
     * @param interactionId The ID of the interaction to like.
     * @param user The user who likes.
     * @return True if the operation was successful, false otherwise.
     */
    @Transactional
    public boolean likeInteraction(Integer interactionId, User user) {
        Optional<Interaction> interactionOpt = interactionRepo.findById(interactionId);

        if (interactionOpt.isEmpty()) {
            return false;
        }

        Interaction interaction = interactionOpt.get();
        User creator = interaction.getUser();

        // 检查用户是否已经点赞过
        if (historyManager.getLikeStatus(user, interaction)) {
            return false; // 已经点赞过，不需要重复操作
        }

        // 添加点赞记录
        historyManager.logLikeInteraction(user, interaction);

        // 增加创作者活跃度
        creator.addActivity(applicationConfig.getActivity().getInteraction().getLike());
        userRepo.save(creator);

        // 增加点赞数
        interaction.likes();
        interactionRepo.save(interaction);

        return true;
    }

    /**
     * Unlike a comment.
     * 
     * @param interactionId The ID of the interaction to unlike.
     * @param user The user who unlikes.
     * @return True if the operation was successful, false otherwise.
     */
    @Transactional
    public boolean unlikeInteraction(Integer interactionId, User user) {
        Optional<Interaction> interactionOpt = interactionRepo.findById(interactionId);

        if (interactionOpt.isEmpty()) {
            return false;
        }

        Interaction interaction = interactionOpt.get();
        User creator = interaction.getUser();

        // 检查用户是否点赞过
        if (!historyManager.getLikeStatus(user, interaction)) {
            return false; // 没有点赞过，无需取消
        }

        // 移除点赞记录
        historyManager.logUnlikeInteraction(user, interaction);

        // 减少创作者活跃度
        creator.addActivity(applicationConfig.getActivity().getInteraction().getUnlike());
        userRepo.save(creator);

        // 减少点赞数
        interaction.unlikes();
        interactionRepo.save(interaction);

        return true;
    }

    /**
     * 检查用户是否对评论点赞
     * 
     * @param interactionId 评论ID
     * @param user 用户
     * @return 是否点赞
     */
    public boolean getUserInteractionStatus(Integer interactionId, User user) {
        Optional<Interaction> interactionOpt = interactionRepo.findById(interactionId);

        if (interactionOpt.isEmpty()) {
            return false;
        }

        Interaction interaction = interactionOpt.get();

        return historyManager.getLikeStatus(user, interaction);
    }

    public Interaction getInteractionById(Integer id) {
        return interactionRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("评论不存在"));
    }
}
