package org.opencourse.repositories;

import org.opencourse.models.History;
import org.opencourse.models.User;
import org.opencourse.utils.typeinfo.ActionType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link History} entities.
 * 
 * @author !EEExp3rt
 */
@Repository
public interface HistoryRepo extends JpaRepository<History, Long> {

    /**
     * Find all histories of the user in descending order of creation date.
     * 
     * @param userId The ID of the user.
     * @return A list of histories associated with the user.
     */
    public List<History> findAllByUserIdOrderByCreatedAtDesc(Integer userId);

    /**
     * Find the latest history record for a user on a specific object in certain interaction actions range.
     * 
     * @param user The user who performed the action.
     * @param objectId The ID of the object related to the action.
     * @param actionTypes The list of action types.
     * @return The most recent history record if found.
     */
    Optional<History> findFirstByUserAndObjectIdAndActionTypeInOrderByCreatedAtDesc(
        User user, Integer objectId, List<ActionType> actionTypes);
}
