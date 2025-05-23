package org.opencourse.repositories;

import org.opencourse.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for {@link User} entities.
 * 
 * @author LJX
 */
@Repository  // 把当前类注册为spring的一个bean
public interface UserRepo extends JpaRepository<User, Integer> {
    // User: 用户实体类
    // Integer: 用户ID的类型 主键类型
    // JpaRepository: 继承JpaRepository接口 提供基本的CRUD操作
    // 不需要实现任何方法 只需要继承JpaRepository接口
    /**
     * Find a user by their email address.
     * @param email The email address.
     * @return The user if found.
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if a user exists by their email address.
     * @param email The email address.
     * @return True if the user exists, false otherwise.
     */
    boolean existsByEmail(String email);
}
