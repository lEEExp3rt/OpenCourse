package org.opencourse.services;

import org.opencourse.dto.request.ResourceUploadDto;
import org.opencourse.models.Course;
import org.opencourse.models.Resource;
import org.opencourse.models.User;
import org.opencourse.repositories.ResourceRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import java.util.List;

/**
 * Resource service manager.
 * 
 * @author !EEExp3rt
 */
@Service
public class ResourceManager {

    private final ResourceRepo resourceRepo; // Data access object.

    /**
     * Constructor.
     * 
     * @param resourceRepo The resource repository.
     */
    @Autowired
    public ResourceManager(ResourceRepo resourceRepo) {
        this.resourceRepo = resourceRepo;
    }

    /**
     * Add a new resource.
     * 
     * @param dto The resource to add.
     * @param file The file to add.
     * @return The added resource.
     */
    @Transactional
    public Resource addResource(ResourceUploadDto dto, MultipartFile file) {
        return null; // TODO: Implement this method.
    }

    /**
     * Update a new resource.
     * 
     * @param dto The resource to update.
     * @param file The file to update.
     * @return The updated resource.
     */
    @Transactional
    public Resource updateResource(ResourceUploadDto dto, MultipartFile file) {
        return null; // TODO: Implement this method.
    }

    /**
     * Delete a resource.
     * 
     * @param dto The resource to delete.
     * @return True if deleted successfully, false otherwise.
     */
    @Transactional
    public Boolean deleteResource(Integer id) {
        return null; // TODO: Implement this method.
    }

    /**
     * Get a resource.
     * 
     * @return The resource.
     */
    public Resource getResource() {
        return null; // TODO: Implement this method.
    }

    /**
     * Get all resources from a course.
     * @param course The course.
     * @return The list of resources.
     */
    public List<Resource> getResources(Course course) {
        return null; // TODO: Implement this method.
    }

    /**
     * Get all resources from a user creator.
     * @param User The user creator.
     * @return The list of resources.
     */
    public List<Resource> getResources(User user) {
        return null; // TODO: Implement this method.
    }

    /**
     * Like a resource.
     */
    public void likeResource() {
        // TODO: Implement this method.
    }

    /**
     * Unlike a resource.
     */
    public void unlikeResource() {
        // TODO: Implement this method.
    }

    /**
     * Dislike a resource.
     */
    public void dislikeResource() {
        // TODO: Implement this method.
    }

    /**
     * Undislike a resource.
     */
    public void undislikeResource() {
        // TODO: Implement this method.
    }

    /**
     * View a resource file.
     * 
     * TODO: Implement this method.
     */
}
