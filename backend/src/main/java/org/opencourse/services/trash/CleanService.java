package org.opencourse.services.trash;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

/**
 * Clean service manager.
 * 
 * @author !EEExp3rt
 */
@Service
public class CleanService {

    /**
     * Constructor.
     */
    @Autowired
    public CleanService() {
    }

    /**
     * Cascade delete all courses on deleting a department.
     */
    @Transactional
    public void cleanCourses() {
        // TODO: Implement this method.
    }

    /**
     * Cascade delete all resources on deleting a course.
     */
    @Transactional
    public void cleanResources() {
        // TODO: Implement this method.
    }

    /**
     * Clean all trashed resource files in file storage system.
     */
    @Transactional
    public void cleanResourceFiles() {
        // TODO: Implement this method.
    }
}