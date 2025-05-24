package org.opencourse.repositories;

import org.opencourse.models.Interaction;
import org.opencourse.models.Course;
import org.opencourse.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; 

/**
 * Repository interface for {@link Interaction} entity.
 * 
 * @author !EEExp3rt
 */
@Repository
public interface InteractionRepo extends JpaRepository<Interaction, Integer> {
    
    /**
     * 查找指定课程的所有评论
     * 
     * @param course 课程
     * @return 评论列表
     */
    List<Interaction> findAllByCourse(Course course);
    
    /**
     * 查找指定课程的所有评论
     * 
     * @param courseId 课程 ID
     * @return 评论列表
     */
    List<Interaction> findAllByCourseId(Short courseId);
    
    /**
     * 查找指定用户的所有评论
     * 
     * @param user 用户
     * @return 评论列表
     */
    List<Interaction> findByUser(User user);
    
    /**
     * 查找指定课程和用户的评论
     * 
     * @param course 课程
     * @param user 用户
     * @return 评论
     */
    Optional<Interaction> findByCourseAndUser(Course course, User user);

    /**
     * Check if a comment exists for the specified course and user.
     * 
     * @param course The course.
     * @param user   The user.
     * @return True if the comment exists, false otherwise.
     */
    boolean existsByCourseAndUser(Course course, User user);
}
