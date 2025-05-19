package org.opencourse.repositories;

import org.opencourse.models.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Department} entities.
 * 
 * @author !EEExp3rt
 */
@Repository
public interface DepartmentRepo extends JpaRepository<Department, Byte> {

    /**
     * Find a department by its name.
     * 
     * @param name The department name.
     * @return The department if found.
     */
    Optional<Department> findByName(String name);

    /**
     * Check if a department with the given name exists.
     * 
     * @param name The department name.
     * @return True if the department exists, false otherwise.
     */
    boolean existsByName(String name);

    /**
     * Find all departments ordered by name.
     * 
     * @return List of departments ordered by name.
     */
    List<Department> findAllByOrderByNameAsc();
}
