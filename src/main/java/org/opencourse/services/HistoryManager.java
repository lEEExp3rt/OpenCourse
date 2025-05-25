package org.opencourse.services;

import org.opencourse.models.*;
import org.opencourse.repositories.HistoryRepo;
import org.opencourse.services.history.HistoryObjectService;
import org.opencourse.utils.typeinfo.ActionType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * History service manager.
 * 
 * @author !EEExp3rt
 */
@Service
public class HistoryManager {

    private final HistoryRepo historyRepo; // Data access object.
    private final HistoryObjectService historyObjectService;

    /**
     * Constructor.
     * 
     * @param historyRepo The history repository.
     * @param historyObjectService The history object service.
     */
    @Autowired
    public HistoryManager(
        HistoryRepo historyRepo,
        HistoryObjectService historyObjectService
    ) {
        this.historyRepo = historyRepo;
        this.historyObjectService = historyObjectService;
    }

    /**
     * Get all histories of the user.
     * 
     * @param userId The user ID.
     * @return The list of histories in descending order of create timestamp.
     */
    public List<History> getHistories(Integer userId) {
        return historyRepo.findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Get the history action object.
     * 
     * @param history The history record.
     * @return The model object associated with the history.
     */
    public Model<? extends Number> getHistoryObject(History history) {
        return historyObjectService.getHistoryObject(history);
    }

    /**
     * Get the like status of a user for a specific interaction.
     * 
     * @param user The user.
     * @param interaction The interaction object.
     * @return True if the user liked the interaction, false otherwise.
     */
    public boolean getLikeStatus(User user, Interaction interaction) {
        History history = historyRepo.findFirstByUserAndObjectIdAndActionTypeInOrderByCreatedAtDesc(
            user,
            interaction.getId(),
            Arrays.asList(
                ActionType.LIKE_INTERACTION,
                ActionType.UNLIKE_INTERACTION
            )
        ).orElse(null);
        return history != null && history.getActionType() == ActionType.LIKE_INTERACTION;
    }

    /**
     * Get the like status of a user for a specific resource.
     * 
     * @param user The user.
     * @return True if the user liked the resource, false otherwise.
     */
    public boolean getLikeStatus(User user, Resource resource) {
        History history = historyRepo.findFirstByUserAndObjectIdAndActionTypeInOrderByCreatedAtDesc(
            user,
            resource.getId(),
            Arrays.asList(
                ActionType.LIKE_RESOURCE,
                ActionType.UNLIKE_RESOURCE
            )
        ).orElse(null);
        return history != null && history.getActionType() == ActionType.LIKE_RESOURCE;
    }

    // Logging methods for different actions.

    @Transactional
    public void logCreateCourse(User user, Course course) throws RuntimeException {
        try {
            historyRepo.save(
                new History(user, ActionType.CREATE_COURSE, course.getId().intValue())
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to log Create-Course", e);
        }
        return ;
    }

    @Transactional
    public void logUpdateCourse(User user, Course course) throws RuntimeException {
        try {
            historyRepo.save(
                new History(user, ActionType.UPDATE_COURSE, course.getId().intValue())
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to log Update-Course", e);
        }
        return ;
    }

    @Transactional
    public void logDeleteCourse(User user, Course course) throws RuntimeException {
        try {
            historyRepo.save(
                new History(user, ActionType.DELETE_COURSE, course.getId().intValue())
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to log Delete-Course", e);
        }
        return ;
    }

    @Transactional
    public void logCreateDepartment(User user, Department department) throws RuntimeException {
        try {
            historyRepo.save(
                new History(user, ActionType.CREATE_DEPARTMENT, department.getId().intValue())
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to log Create-Department", e);
        }
        return ;
    }

    @Transactional
    public void logUpdateDepartment(User user, Department department) throws RuntimeException {
        try {
            historyRepo.save(
                new History(user, ActionType.UPDATE_DEPARTMENT, department.getId().intValue())
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to log Update-Department", e);
        }
        return ;
    }

    @Transactional
    public void logDeleteDepartment(User user, Department department) throws RuntimeException {
        try {
            historyRepo.save(
                new History(user, ActionType.DELETE_DEPARTMENT, department.getId().intValue())
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to log Delete-Department", e);
        }
        return ;
    }

    @Transactional
    public void logCreateResource(User user, Resource resource) throws RuntimeException {
        try {
            historyRepo.save(new History(user, ActionType.CREATE_RESOURCE, resource.getId()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to log Create-Resource", e);
        }
        return ;
    }

    @Transactional
    public void logUpdateResource(User user, Resource resource) throws RuntimeException {
        try {
            historyRepo.save(new History(user, ActionType.UPDATE_RESOURCE, resource.getId()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to log Update-Resource", e);
        }
        return ;
    }

    @Transactional
    public void logDeleteResource(User user, Resource resource) throws RuntimeException {
        try {
            historyRepo.save(new History(user, ActionType.DELETE_RESOURCE, resource.getId()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to log Delete-Resource", e);
        }
        return ;
    }

    @Transactional
    public void logLikeResource(User user, Resource resource) throws RuntimeException {
        try {
            historyRepo.save(new History(user, ActionType.LIKE_RESOURCE, resource.getId()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to log Like-Resource", e);
        }
        return ;
    }

    @Transactional
    public void logUnlikeResource(User user, Resource resource) throws RuntimeException {
        try {
            historyRepo.save(new History(user, ActionType.UNLIKE_RESOURCE, resource.getId()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to log Unlike-Resource", e);
        }
        return ;
    }

    @Transactional
    public void logViewResource(User user, Resource resource) throws RuntimeException {
        try {
            historyRepo.save(new History(user, ActionType.VIEW_RESOURCE, resource.getId()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to log View-Resource", e);
        }
        return ;
    }

    @Transactional
    public void logCreateInteraction(User user, Interaction interaction) throws RuntimeException {
        try {
            historyRepo.save(new History(user, ActionType.CREATE_INTERACTION, interaction.getId()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to log Create-Interaction", e);
        }
        return ;
    }

    @Transactional
    public void logUpdateInteraction(User user, Interaction interaction) throws RuntimeException {
        try {
            historyRepo.save(new History(user, ActionType.UPDATE_INTERACTION, interaction.getId()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to log Update-Interaction", e);
        }
        return ;
    }

    @Transactional
    public void logDeleteInteraction(User user, Interaction interaction) throws RuntimeException {
        try {
            historyRepo.save(new History(user, ActionType.DELETE_INTERACTION, interaction.getId()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to log Delete-Interaction", e);
        }
        return ;
    }

    @Transactional
    public void logLikeInteraction(User user, Interaction interaction) throws RuntimeException {
        try {
            historyRepo.save(new History(user, ActionType.LIKE_INTERACTION, interaction.getId()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to log Like-Interaction", e);
        }
        return ;
    }

    @Transactional
    public void logUnlikeInteraction(User user, Interaction interaction) throws RuntimeException {
        try {
            historyRepo.save(new History(user, ActionType.UNLIKE_INTERACTION, interaction.getId()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to log Unlike-Interaction", e);
        }
        return ;
    }

    @Transactional
    public void logRateCourse(User user, Course course) throws RuntimeException {
        try {
            historyRepo.save(new History(user, ActionType.RATE_COURSE, course.getId().intValue()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to log Rate-Course", e);
        }
        return ;
    }

    @Transactional
    public void logCreateUser(User user) throws RuntimeException {
        try {
            historyRepo.save(new History(user, ActionType.CREATE_USER));
        } catch (Exception e) {
            throw new RuntimeException("Failed to log Create-User", e);
        }
        return ;
    }

    @Transactional
    public void logUpdateUser(User user) throws RuntimeException {
        try {
            historyRepo.save(new History(user, ActionType.UPDATE_USER));
        } catch (Exception e) {
            throw new RuntimeException("Failed to log Update-User", e);
        }
        return ;
    }

    @Transactional
    public void logDeleteUser(User user) throws RuntimeException {
        try {
            historyRepo.save(new History(user, ActionType.DELETE_USER));
        } catch (Exception e) {
            throw new RuntimeException("Failed to log Delete-User", e);
        }
        return ;
    }
}
