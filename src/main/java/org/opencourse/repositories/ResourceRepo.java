package org.opencourse.repositories;

import org.opencourse.models.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link Resource} entity.
 * 
 * @author !EEExp3rt
 */
@Repository
public interface ResourceRepo extends JpaRepository<Resource, Integer> {
}
