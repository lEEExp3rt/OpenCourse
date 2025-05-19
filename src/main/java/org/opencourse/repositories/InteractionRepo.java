package org.opencourse.repositories;

import org.opencourse.models.Interaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link Interaction} entity.
 * 
 * @author !EEExp3rt
 */
@Repository
public interface InteractionRepo extends JpaRepository<Interaction, Integer> {
}
