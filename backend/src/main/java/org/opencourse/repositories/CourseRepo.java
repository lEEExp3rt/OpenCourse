package org.opencourse.repositories;

import org.opencourse.models.Course;
import org.opencourse.utils.typeinfo.CourseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Course} entities.
 * 
 * @author !EEExp3rt
 */
@Repository
public interface CourseRepo extends JpaRepository<Course, Short> {

    /**
     * Find courses fuzzy matching the given name.
     * 
     * @param name The course name.
     * @return List of matching courses in name ascending order.
     */
    List<Course> findByNameContainingIgnoreCaseOrderByNameAsc(String name);

    /**
     * Find a course by its code.
     * 
     * @param code The course code.
     * @return The course if found.
     */
    Optional<Course> findByCode(String code);

    /**
     * Find courses fuzzy matching the given code.
     * 
     * @param code The course code.
     * @return List of matching courses in name ascending order.
     */
    List<Course> findByCodeContainingIgnoreCaseOrderByNameAsc(String code);

    /**
     * Check if a course with the given code exists.
     * 
     * @param code The course code.
     * @return True if exists, false otherwise.
     */
    boolean existsByCode(String code);

    /**
     * Find all courses belonging to a specific department.
     * 
     * @param departmentId The department ID.
     * @return List of courses in the department.
     */
    List<Course> findByDepartmentId(Byte departmentId);

    /**
     * Find all courses of a specific course type.
     * 
     * @param courseType The course type.
     * @return List of courses with the specified type.
     */
    List<Course> findByCourseType(CourseType courseType);

    /**
     * Find all courses of a specific course type and in a department.
     * 
     * @param departmentId The department ID.
     * @param courseType   The course type.
     * @return List of courses matching the condition.
     */
    List<Course> findByDepartmentIdAndCourseType(Byte departmentId, CourseType courseType);

    /**
     * Find all courses ordered by name.
     * 
     * @return List of all courses ordered by name.
     */
    List<Course> findAllByOrderByNameAsc();
}
