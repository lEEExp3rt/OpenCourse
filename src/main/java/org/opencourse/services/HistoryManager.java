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
     * Get the history object.
     * 
     * @param history The history record.
     * @return The model object associated with the history.
     */
    public Model<? extends Number> getHistoryObject(History history) {
        return historyObjectService.getHistoryObject(history);
    }

    /**
     * Add a history record.
     * 
     * @param user   The user who performed the action.
     * @param type   The action type.
     * @param object The object on which the action was performed.
     */
    @Transactional
    public void addHistory(User user, ActionType type, Model<? extends Number> object) {
        historyRepo.save(new History(user, type, object.getId().intValue()));
    }
    
    /**
     * Add a history record.
     * 
     * @param userId The ID of the user who performed the action.
     * @param type   The action type.
     * @param object The object on which the action was performed.
     */
    @Transactional
    public void addHistory(Integer userId, ActionType type, Model<? extends Number> object) {
        User user = userRepo.findById(userId).get();
        historyRepo.save(new History(user, type, object.getId().intValue()));
    }
    
    /**
     * Add a history record.
     * 
     * @param user     The user who performed the action.
     * @param type     The action type.
     */
    @Transactional
    public void addHistory(User user, ActionType type) {
        historyRepo.save(new History(user, type));
    }
    
    /**
     * Add a history record.
     * 
     * @param userId   The ID of the user who performed the action.
     * @param type     The action type.
     */
    @Transactional
    public void addHistory(Integer userId, ActionType type) {
        User user = userRepo.findById(userId).get();
        historyRepo.save(new History(user, type));
    }
    
    /**
     * 检查用户是否对某个对象执行过特定操作
     * 
     * @param user 用户
     * @param type 操作类型
     * @param object 操作对象
     * @return true如果存在相应记录，否则false
     */
    public boolean hasPerformedAction(User user, ActionType type, Model<? extends Number> object) {
        return historyRepo.existsByUserAndActionTypeAndObjectId(user, type, object.getId().intValue());
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
    public boolean removeAction(User user, ActionType type, Model<? extends Number> object) {
        History history = historyRepo
            .findByUserAndActionTypeAndObjectId(user, type, object.getId().intValue())
            .orElse(null);
        if (history != null) {
            historyRepo.delete(history);
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
    public List<History> getActionsByUserAndObject(User user, Integer objectId) {
        return historyRepo.findAllByUserAndObjectId(user, objectId);
    }
}
