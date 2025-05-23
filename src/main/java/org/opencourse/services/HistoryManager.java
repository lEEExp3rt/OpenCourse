package org.opencourse.services;

import org.opencourse.models.ActionObject;
import org.opencourse.models.History;
import org.opencourse.models.User;
import org.opencourse.repositories.HistoryRepo;
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

    /**
     * Constructor.
     * 
     * @param historyRepo The history repository
     */
    @Autowired
    public HistoryManager(HistoryRepo historyRepo) {
        this.historyRepo = historyRepo;
    }

    /**
     * Add a history record.
     * 
     * @param user The user who performed the action.
     * @param type The action type.
     * @return True if the history was added successfully, false otherwise.
     */
    @Transactional
    public Boolean addHistory(User user, ActionType type) {
        History history = new History(user, type);
        return historyRepo.save(history) != null;
    }

    /**
     * Add a history record.
     * 
     * @param user The user who performed the action.
     * @param type The action type.
     * @param object The action object.
     * @return True if the history was added successfully, false otherwise.
     */
    @Transactional
    public Boolean addHistory(User user, ActionType type, ActionObject object) {
        History history = new History(user, type, object);
        return historyRepo.save(history) != null;
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
}
