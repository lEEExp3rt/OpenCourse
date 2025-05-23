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
@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
    
    /**
     * Find a user by their username.
     * @param name The username.
     * @return The user if found.
     */
    Optional<User> findByName(String name);

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
