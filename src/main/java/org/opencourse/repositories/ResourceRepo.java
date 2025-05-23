package org.opencourse.repositories;

import org.opencourse.models.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for {@link Resource} entity.
 * 
 * @author !EEExp3rt
 */
@Repository
public interface ResourceRepo extends JpaRepository<Resource, Integer> {

    /**
     * Find resources by course ID.
     * 
     * @param courseId The course ID.
     * @return A list of resources associated with the course.
     */
    public List<Resource> findByCourseId(Short courseId);

    /**
     * Find resources by user ID.
     * 
     * @param userId The user ID.
     * @return A list of resources associated with the user.
     */
    public List<Resource> findByUserId(Integer userId);
}
