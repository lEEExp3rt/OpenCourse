package org.opencourse.services;

import org.opencourse.models.*;
import org.opencourse.repositories.HistoryRepo;
import org.opencourse.repositories.UserRepo;
import org.opencourse.services.history.HistoryObjectService;
import org.opencourse.utils.typeinfo.ActionType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * History service manager.
 * 
 * @author !EEExp3rt
 */
@Service
public class HistoryManager {

    private final HistoryRepo historyRepo; // Data access object.
    private final UserRepo userRepo; // Data access object.
    private final HistoryObjectService historyObjectService;

    /**
     * Constructor.
     * 
     * @param historyRepo The history repository.
     * @param userRepo The user repository.
     */
    @Autowired
    public HistoryManager(
        HistoryRepo historyRepo,
        UserRepo userRepo,
        HistoryObjectService historyObjectService
    ) {
        this.historyRepo = historyRepo;
        this.userRepo = userRepo;
        this.historyObjectService = historyObjectService;
    }

    /**
     * Get histories.
     * 
     * @param userId The user ID.
     * @return The list of histories.
     */
    public List<History> getHistories(Integer userId) {
        return historyRepo.findAllByUserId(userId);
    }
    
    /**
     * 检查用户是否对某个对象执行过特定操作
     * 
     * @param user 用户
     * @param type 操作类型
     * @param object 操作对象
     * @return true如果存在相应记录，否则false
     */
    public boolean hasPerformedAction(User user, ActionType type, ActionObject object) {
        Optional<History> history = historyRepo.findByUserAndActionTypeAndActionObject(user, type, object);
        return history.isPresent();
    }
    
    /**
     * 移除用户对某个对象的特定操作记录
     * 
     * @param user 用户
     * @param type 操作类型
     * @param object 操作对象
     * @return true如果成功删除，否则false
     */
    @Transactional
    public boolean removeAction(User user, ActionType type, ActionObject object) {
        Optional<History> history = historyRepo.findByUserAndActionTypeAndActionObject(user, type, object);
        if (history.isPresent()) {
            historyRepo.delete(history.get());
            return true;
        }
        return false;
    }
    
    /**
     * 获取用户对某个对象的所有操作记录
     * 
     * @param user 用户
     * @param object 操作对象
     * @return 历史记录列表
     */
    public List<History> getActionsByUserAndObject(User user, ActionObject object) {
        return historyRepo.findByUserAndActionObject(user, object);
    }
    
    /**
     * 检查用户是否对某个评论点过赞
     * 
     * @param user 用户
     * @param interaction 评论对象
     * @return true如果点过赞，否则false
     */
    public boolean hasLiked(User user, ActionObject interaction) {
        return hasPerformedAction(user, ActionType.LIKE_INTERACTION, interaction);
    }
    
    /**
     * 检查用户是否对某个评论点过踩
     * 
     * @param user 用户
     * @param interaction 评论对象
     * @return true如果点过踩，否则false
     */
    public boolean hasDisliked(User user, ActionObject interaction) {
        return hasPerformedAction(user, ActionType.DISLIKE_INTERACTION, interaction);
    }

    // Course actions.

    @Transactional
    public void logCourseCreation() {
        // TODO: Implement this method.
    }

    @Transactional
    public void logCourseUpdate() {
        // TODO: Implement this method.
    }

    @Transactional
    public void logCourseRate() {
        // TODO: Implement this method.
    }

    // Department actions.

    @Transactional
    public void logDepartmentCreation() {
        // TODO: Implement this method.
    }

    @Transactional
    public void logDepartmentUpdate() {
        // TODO: Implement this method.
    }

    // Resource actions.

    @Transactional
    public void logResourceCreation() {
        // TODO: Implement this method.
    }

    @Transactional
    public void logResourceDelete() {
        // TODO: Implement this method.
    }

    @Transactional
    public void logResourceLike() {
        // TODO: Implement this method.
    }

    @Transactional
    public void logResourceUnlike() {
        // TODO: Implement this method.
    }

    @Transactional
    public void logResourceView() {
        // TODO: Implement this method.
    }

    // Interaction actions.

    @Transactional
    public void logInteractionCreation() {
        // TODO: Implement this method.
    }

    @Transactional
    public void logInteractionDelete() {
        // TODO: Implement this method.
    }

    @Transactional
    public void logInteractionLike() {
        // TODO: Implement this method.
    }

    @Transactional
    public void logInteractionUnlike() {
        // TODO: Implement this method.
    }

    // User actions.

    @Transactional
    public void logUserCreation() {
        // TODO: Implement this method.
    }

    @Transactional
    public void logUserUpdate() {
        // TODO: Implement this method.
    }
}
