package org.opencourse.repositories;

import org.opencourse.models.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link History} entities.
 * 
 * @author !EEExp3rt
 */
@Repository
public interface HistoryRepo extends JpaRepository<History, Long> {
}
