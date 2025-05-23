package org.opencourse.repositories;

import org.opencourse.models.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
