package org.opencourse.repositories;

import org.opencourse.models.Interaction;
import org.opencourse.models.User;
import org.opencourse.models.UserInteractionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户交互记录仓库接口
 */
@Repository
public interface UserInteractionRecordRepo extends JpaRepository<UserInteractionRecord, Integer> {
    
    /**
     * 查找特定用户对特定评论的交互记录
     * 
     * @param user 用户
     * @param interaction 评论
     * @return 交互记录
     */
    Optional<UserInteractionRecord> findByUserAndInteraction(User user, Interaction interaction);
} 