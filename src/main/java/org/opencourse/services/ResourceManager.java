package org.opencourse.services;

import org.opencourse.dto.request.ResourceUploadDto;
import org.opencourse.models.Course;
import org.opencourse.models.Resource;
import org.opencourse.models.Resource.ResourceFile;
import org.opencourse.models.User;
import org.opencourse.repositories.CourseRepo;
import org.opencourse.repositories.ResourceRepo;
import org.opencourse.repositories.UserRepo;
import org.opencourse.services.storage.FileStorageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import java.io.InputStream;
import java.util.List;

/**
 * Resource service manager.
 * 
 * @author !EEExp3rt
 */
@Service
public class ResourceManager {

    private final CourseRepo courseRepo;
    private final ResourceRepo resourceRepo;
    private final UserRepo userRepo;
    private final FileStorageService fileStorageService;

    /**
     * Constructor.
     * 
     * @param courseRepo         The course repository.
     * @param resourceRepo       The resource repository.
     * @param userRepo           The user repository.
     * @param fileStorageService The file storage service.
     */
    @Autowired
    public ResourceManager(
        CourseRepo courseRepo,
        ResourceRepo resourceRepo,
        UserRepo userRepo,
        FileStorageService fileStorageService
    ) {
        this.courseRepo = courseRepo;
        this.resourceRepo = resourceRepo;
        this.userRepo = userRepo;
        this.fileStorageService = fileStorageService;
    }

    /**
     * Add a new resource.
     * 
     * @param dto  The resource metadata.
     * @param file The file to add.
     * @return The added resource.
     * @throws IllegalArgumentException If the course or user is not found.
     * @throws RuntimeException         If the file storage fails or other error happens.
     */
    @Transactional
    public Resource addResource(ResourceUploadDto dto, MultipartFile file) {
        ResourceFile resourceFile = null;
        try {
            // Get course and user infomation.
            Course course = courseRepo.findById(dto.getCourseId()).orElse(null);
            User user = userRepo.findById(dto.getUserId()).orElse(null);
            if (course == null || user == null) {
                throw new IllegalArgumentException("Course or user not found");
            }
            // Store the file.
            resourceFile = fileStorageService.storeFile(
                file,
                dto.getFileType(),
                course.getId()
            );
            if (resourceFile == null) {
                throw new RuntimeException("Failed to store file " + file.getOriginalFilename());
            }
            // Create the resource.
            Resource resource = new Resource(
                dto.getName(),
                dto.getDescription(),
                dto.getResourceType(),
                resourceFile,
                course,
                user
            );
            // Save the resource.
            return resourceRepo.save(resource);
        } catch (Exception e) {
            // If any error occurs, delete the file if it was created.
            if (resourceFile != null) {
                try {
                    fileStorageService.deleteFile(resourceFile.getFilePath());
                } catch (Exception deleteException) {
                    throw new RuntimeException("Failed to delete file while rollbacking", deleteException);
                }
            }
            throw e;
        }
    }

    /**
     * Delete a resource.
     * 
     * @param id The resource id.
     * @return True if deleted successfully, false otherwise.
     */
    @Transactional
    public Boolean deleteResource(Integer id) {
        return null; // TODO: Implement this method.
    }

    /**
     * Get a resource.
     * 
     * @param id The resource id.
     * @return The resource entity.
     */
    public Resource getResource(Integer id) {
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
     * 
     * @param id The resource id.
     */
    public void likeResource(Integer id) {
        // TODO: Implement this method.
    }

    /**
     * Unlike a resource.
     * 
     * @param id The resource id.
     */
    public void unlikeResource(Integer id) {
        // TODO: Implement this method.
    }

    /**
     * Dislike a resource.
     * 
     * @param id The resource id.
     */
    public void dislikeResource(Integer id) {
        // TODO: Implement this method.
    }

    /**
     * Undislike a resource.
     * 
     * @param id The resource id.
     */
    public void undislikeResource(Integer id) {
        // TODO: Implement this method.
    }

    /**
     * View a resource file.
     * 
     * @param id The resource id.
     */
    public InputStream viewResource(Integer id) {
        return null;
    }
}
