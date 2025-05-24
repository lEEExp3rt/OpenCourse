package org.opencourse.repositories;

import org.opencourse.models.ActionObject;
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
     * 查找用户对特定对象的指定操作记录
     * 
     * @param user 用户
     * @param actionType 操作类型
     * @param actionObject 操作对象
     * @return 历史记录（如果存在）
     */
    Optional<History> findByUserAndActionTypeAndActionObject(User user, ActionType actionType, ActionObject actionObject);
    
    /**
     * 查找用户对特定对象的所有操作记录
     * 
     * @param user 用户
     * @param actionObject 操作对象
     * @return 历史记录列表
     */
    List<History> findByUserAndActionObject(User user, ActionObject actionObject);
}
