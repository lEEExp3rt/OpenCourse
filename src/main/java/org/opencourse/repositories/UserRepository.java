package org.opencourse.repositories;

import org.opencourse.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问接口
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    /**
     * 根据邮箱查找用户
     * @param email 用户邮箱
     * @return 可能存在的用户
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 检查邮箱是否已存在
     * @param email 用户邮箱
     * @return 是否存在
     */
    boolean existsByEmail(String email);
}
