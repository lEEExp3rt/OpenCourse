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
