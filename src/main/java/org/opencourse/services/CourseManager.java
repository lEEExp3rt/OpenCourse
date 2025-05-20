package org.opencourse.services;

import org.opencourse.models.Course;
import org.opencourse.repositories.CourseRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Course service manager.
 * 
 * @author !EEExp3rt
 */
@Service
public class CourseManager {

    // Data Access Object.
    private final CourseRepo repo;

    /**
     * Constructor.
     * 
     * @param repo The course repository.
     */
    @Autowired
    public CourseManager(CourseRepo repo) {
        this.repo = repo;
    }

    public Course addCourse() {
        return null; // TODO
    }

}