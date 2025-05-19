package org.opencourse.repositories;

import org.opencourse.models.Course;
import org.opencourse.models.Department;
import org.opencourse.utils.typeinfo.CourseType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
     * Find a course by its code.
     * 
     * @param code The course code.
     * @return The course if found.
     */
    Optional<Course> findByCode(String code);

    /**
     * Find courses containing the given name (case insensitive).
     * 
     * @param name The course name to search.
     * @return List of matching courses.
     */
    List<Course> findByNameContainingIgnoreCase(String name);

    /**
     * Find all courses belonging to a specific department.
     * 
     * @param department The department.
     * @return List of courses in the department.
     */
    List<Course> findByDepartment(Department department);

    /**
     * Find all courses of a specific course type.
     * 
     * @param courseType The course type.
     * @return List of courses with the specified type.
     */
    List<Course> findByCourseType(CourseType courseType);

    /**
     * Find courses with credits in the given range.
     * 
     * @param minCredits Minimum credits.
     * @param maxCredits Maximum credits.
     * @return List of courses within the credits range.
     */
    List<Course> findByCreditsGreaterThanEqualAndCreditsLessThanEqual(Float minCredits, Float maxCredits);

    /**
     * Check if a course with the given code exists.
     * 
     * @param code The course code.
     * @return True if exists, false otherwise.
     */
    boolean existsByCode(String code);

    /**
     * Find all courses ordered by name.
     * 
     * @return List of all courses ordered by name.
     */
    List<Course> findAllByOrderByNameAsc();

    /**
     * Find all courses ordered by credits.
     * 
     * @return List of all courses ordered by credits.
     */
    List<Course> findAllByOrderByCreditsDesc();

    /**
     * Find courses by department and course type.
     * 
     * @param department The department.
     * @param courseType The course type.
     * @return List of matching courses.
     */
    List<Course> findByDepartmentAndCourseType(Department department, CourseType courseType);

    /**
     * Search courses with pagination.
     * 
     * @param keyword Search keyword for name or code.
     * @param pageable Pagination information.
     * @return Page of matching courses.
     */
    @Query("SELECT c FROM Course c WHERE c.name LIKE %:keyword% OR c.code LIKE %:keyword%")
    Page<Course> searchCourses(String keyword, Pageable pageable);
}
