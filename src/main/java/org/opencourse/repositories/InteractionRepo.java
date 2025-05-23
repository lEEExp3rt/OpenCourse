package org.opencourse.repositories;

import org.opencourse.models.Interaction;
import org.opencourse.models.Course;
import org.opencourse.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    List<Interaction> findByCourse(Course course);
    
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
    Interaction findByCourseAndUser(Course course, User user);
}
