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
     * Find all histories by user ID.
     * 
     * @param userId The ID of the user.
     * @return A list of histories associated with the user.
     */
    public List<History> findAllByUserId(Integer userId);

    /**
     * Find all histories by user and object ID.
     * 
     * @param user The user associated with the histories.
     * @return A list of histories associated with the user.
     */
    public List<History> findAllByUserAndObjectId(User user, Integer objectId);

    /**
     * 查找用户对特定对象的指定操作记录
     * 
     * @param user 用户
     * @param actionType 操作类型
     * @param objectId 操作对象 ID
     * @return 历史记录（如果存在）
     */
    Optional<History> findByUserAndActionTypeAndObjectId(User user, ActionType actionType, Integer objectId);

    /**
     * Check if a history record exists for a user with a specific action type and object ID.
     * 
     * @param user       The user associated with the history record.
     * @param actionType The action type associated with the history record.
     * @param objectId   The object ID associated with the history record.
     * @return true if the history record exists, false otherwise.
     */
    boolean existsByUserAndActionTypeAndObjectId(User user, ActionType actionType, Integer objectId);
}
