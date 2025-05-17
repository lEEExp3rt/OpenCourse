package org.opencourse.repositories;

/**
 * Type repository for OpenCourse to provide DAO functionality.
 * 
 * @author !EEExp3rt
 */

import org.opencourse.models.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Type repository for managing type entities.
 */
@Repository
public interface TypeRepository extends JpaRepository<Type, Byte> {
}
